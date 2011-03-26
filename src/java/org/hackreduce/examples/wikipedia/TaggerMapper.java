package org.hackreduce.examples.wikipedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.binding.http.strategy.EnglishInflector;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.hackreduce.examples.wikipedia.Tagger.TaggerCount;
import org.hackreduce.mappers.WikipediaMapper;
import org.hackreduce.models.WikipediaRecord;

import edu.northwestern.at.utils.corpuslinguistics.tokenizer.DefaultWordTokenizer;
import edu.northwestern.at.utils.corpuslinguistics.tokenizer.WordTokenizer;


/**
 * This MapReduce job will map Wikipedia data to actual workable data for the tagger algorithm.
 *
 */

public class TaggerMapper extends WikipediaMapper<Text, LongWritable> {

	// Our own made up key to send all counts to a single Reducer, so we can
	// aggregate a total value.
	public static final Text TOTAL_COUNT = new Text("total");

	// Just to save on object instantiation
	public static final LongWritable ONE_COUNT = new LongWritable(1);

	public static final Pattern TO_KEEP = Pattern.compile("^[a-z]+$");
	@Override
	protected void map(WikipediaRecord record, Context context) throws IOException, InterruptedException {
		String text = record.getRawText();
		ArrayList<String> categories = record.getCategories();

		EnglishInflector eng = new EnglishInflector();
		WordTokenizer tokenizer = new DefaultWordTokenizer();
		List<String> words = tokenizer.extractWords(text);
		
		for(String word : words) {
			word = eng.singularlize(word).toLowerCase();
			if (TO_KEEP.matcher(word).matches()) {
				for (String category : categories) {
					context.write(new Text(category + ":" + word), new LongWritable(1));
				}
			}
		}

		context.getCounter(TaggerCount.ARTICLES_PARSED).increment(1);
	}

}
