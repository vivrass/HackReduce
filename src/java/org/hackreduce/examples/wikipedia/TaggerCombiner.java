package org.hackreduce.examples.wikipedia;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.hackreduce.examples.wikipedia.Tagger.TaggerCount;

public class TaggerCombiner extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	@Override
	protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
		try {
			context.getCounter(TaggerCount.UNIQUE_KEYS).increment(1);

			context.write(key, new DoubleWritable(calculateCount(values)));
		} catch (Exception e) {
			
		}
	}
	
	double calculateCount(Iterable<DoubleWritable> values) {
		long count = 0;
		double finalValue = 0;
		
		for (DoubleWritable value : values) {
//			System.out.println(value);
			finalValue += value.get();
			count += 1;
		}
		return finalValue/count;
	}

}

