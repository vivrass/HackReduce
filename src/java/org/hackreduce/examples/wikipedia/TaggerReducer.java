package org.hackreduce.examples.wikipedia;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.hackreduce.examples.wikipedia.Tagger.TaggerCount;

public class TaggerReducer extends TaggerCombiner {

	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
		try {
			context.getCounter(TaggerCount.UNIQUE_KEYS).increment(1);

			double count = calculateCount(values);

			context.write(key, new DoubleWritable(count));
		} catch (Exception e) {
			
		}
	}

}
