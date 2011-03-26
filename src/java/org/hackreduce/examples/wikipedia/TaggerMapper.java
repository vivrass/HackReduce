package org.hackreduce.examples.wikipedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.hackreduce.examples.wikipedia.Tagger.TaggerCount;
import org.hackreduce.mappers.WikipediaMapper;
import org.hackreduce.models.WikipediaRecord;

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

	@Override
	protected void map(WikipediaRecord record, Context context) throws IOException, InterruptedException {
		String text = record.getText();
		ArrayList<String> categories = record.getCategories();

		Pattern p = Pattern.compile("[\\s\\p{P}]?([a-zA-Z]+)");
		Matcher m = p.matcher(text);
		while(m.find()) {
			for (String category : categories) {
				context.write(new Text(category + ":" + m.group(1)), new LongWritable(1));
			}
		}

		context.getCounter(TaggerCount.ARTICLES_PARSED).increment(1);
	}
}
