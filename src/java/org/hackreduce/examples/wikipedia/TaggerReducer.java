package org.hackreduce.examples.wikipedia;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.hackreduce.examples.wikipedia.Tagger.TaggerCount;

public class TaggerReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

	@Override
	protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
		context.getCounter(TaggerCount.UNIQUE_KEYS).increment(1);
		context.getCounter(TaggerCount.TAGGER_KEYS).increment(1);

		long count = 0;
		for (LongWritable value : values) {
			count += value.get();
		}

		context.write(key, new LongWritable(count));
	}

}
