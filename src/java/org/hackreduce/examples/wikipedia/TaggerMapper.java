package org.hackreduce.examples.wikipedia;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.hackreduce.mappers.WikipediaMapper;
import org.hackreduce.models.WikipediaRecord;


/**
 * This MapReduce job will map Wikipedia data to actual workable data for the tagger algorithm.
 *
 */
public class TaggerMapper extends org.hackreduce.examples.wikipedia.Tagger {

	public static class WikipediaParser extends WikipediaMapper<Text, LongWritable> {

		// Our own made up key to send all counts to a single Reducer, so we can
		// aggregate a total value.
		public static final Text TOTAL_COUNT = new Text("total");

		// Just to save on object instantiation
		public static final LongWritable ONE_COUNT = new LongWritable(1);

		@Override
		protected void map(WikipediaRecord record, Context context) throws IOException,
				InterruptedException {

			String text = record.getText();
			ArrayList<String> categories = record.getCategories();
			for (String category : categories) {
				for (String word : text.split(" ")) {
					context.write(new Text(category + ":" + word), new LongWritable(1));
				}
			}
		}
	}
}
