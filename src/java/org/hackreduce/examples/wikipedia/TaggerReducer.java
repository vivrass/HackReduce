package org.hackreduce.examples.wikipedia;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.hackreduce.examples.wikipedia.Tagger.TaggerCount;

public class TaggerReducer extends TaggerCombiner {

	@Override
	protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
		try {
			context.getCounter(TaggerCount.UNIQUE_KEYS).increment(1);

			long count = calculateCount(values);

			if (count > 5)
				context.write(key, new LongWritable(count));
		} catch(Exception e) {
			
		}
	}

}
