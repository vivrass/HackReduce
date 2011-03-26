package org.hackreduce.examples.wikipedia;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.hackreduce.examples.wikipedia.TaggerMapper;
import org.hackreduce.mappers.ModelMapper;
import org.hackreduce.mappers.XMLInputFormat;
import org.hackreduce.mappers.XMLRecordReader;


/**
 * This MapReduce job will count the total number of Bixi records in the data dump.
 *
 */
public class Tagger extends Configured implements Tool {

	public enum TaggerCount {
		UNIQUE_KEYS,
		ARTICLES_PARSED,
		ARTICLES_IGNORED
	}

	public Class<? extends ModelMapper<?, ?, ?, ?, ?>> getMapper() {
		return TaggerMapper.class;
	}
	public Class<? extends Reducer<?, ?, ?, ?>> getReducer() {
		return TaggerReducer.class;
	}
	public Class<? extends Reducer<?, ?, ?, ?>> getCombiner() {
		return TaggerCombiner.class;
	}

	public void configureJob(Job job) {
		// The Wikipedia format come in XML, so we configure
		// the job to use this format.
		job.setInputFormatClass(XMLInputFormat.class);
		XMLRecordReader.setRecordTags(job, "<page>", "</page>");
	};

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();

        if (args.length != 2) {
        	System.err.println("Usage: " + getClass().getName() + " <input> <output>");
        	System.exit(2);
        }

        // Creating the MapReduce job (configuration) object
        Job job = new Job(conf);
        job.setJarByClass(getClass());
        job.setJobName(getClass().getName());

        // Tell the job which Mapper and Reducer to use (classes defined above)
        job.setMapperClass(getMapper());
		job.setReducerClass(getReducer());
		job.setCombinerClass(getCombiner());
		
		job.setNumReduceTasks(400);

        configureJob(job);

		// This is what the Mapper will be outputting to the Reducer
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);

		// This is what the Reducer will be outputting
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

		// Setting the input folder of the job 
		FileInputFormat.addInputPath(job, new Path(args[0]));

		// Preparing the output folder by first deleting it if it exists
        Path output = new Path(args[1]);
        FileSystem.get(conf).delete(output, true);
	    FileOutputFormat.setOutputPath(job, output);

		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new Configuration(), new Tagger(), args);
		System.exit(result);
	}
}
