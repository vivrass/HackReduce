package org.hackreduce.examples.wikipedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.binding.http.strategy.EnglishInflector;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.hackreduce.examples.wikipedia.Tagger.TaggerCount;
import org.hackreduce.mappers.WikipediaMapper;
import org.hackreduce.models.WikipediaRecord;

import edu.northwestern.at.utils.corpuslinguistics.stopwords.DefaultStopWords;
import edu.northwestern.at.utils.corpuslinguistics.stopwords.StopWords;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.DefaultWordTokenizer;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.WordTokenizer;


/**
 * This MapReduce job will map Wikipedia data to actual workable data for the tagger algorithm.
 *
 */

public class TaggerMapper extends WikipediaMapper<Text, DoubleWritable> {

	// Our own made up key to send all counts to a single Reducer, so we can
	// aggregate a total value.
	public static final Text TOTAL_COUNT = new Text("total");

	// Just to save on object instantiation
	public static final DoubleWritable ONE_COUNT = new DoubleWritable(1);

	public static final Pattern TO_KEEP = Pattern.compile("^[a-z]+$");
	@Override
	protected void map(WikipediaRecord record, Context context) throws IOException, InterruptedException {
		try {
			String text = record.getRawText();
			ArrayList<String> categories = record.getCategories();

			if ( categories.size() == 0)
			{
				context.getCounter(TaggerCount.ARTICLES_IGNORED).increment(1);
				return;
			}

			StopWords stopWords = new DefaultStopWords();
			EnglishInflector eng = new EnglishInflector();
			WordTokenizer tokenizer = new DefaultWordTokenizer();
			List<String> words = tokenizer.extractWords(text);
			Hashtable<String, Long> uniqueValues = new Hashtable<String, Long>();

			long currentCount = 0;
			for(String word : words) {
				word = eng.singularlize(word).toLowerCase();
				if (stopWords.isStopWord(word))
					continue;
				if (TO_KEEP.matcher(word).matches()) {
					if( uniqueValues.containsKey(word) )
						currentCount = (Long) uniqueValues.get(word) + 1;
					else
						currentCount = 1;
					uniqueValues.put(word, currentCount);
				}
			}

			Enumeration<String> keys = uniqueValues.keys();
			String key;
			int count = words.size();
			while(keys.hasMoreElements()) {
				key = keys.nextElement();
				double value = ((double)uniqueValues.get(key))/count;
				for (String category : categories) {
					context.write(new Text(category + ":" + key), new DoubleWritable(value));
				}
			}

			context.getCounter(TaggerCount.ARTICLES_PARSED).increment(1);
		} catch (Exception e) {
			
		}
	}

}
