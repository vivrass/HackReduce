package org.hackreduce.examples.wikipedia;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.ToolRunner;
import org.hackreduce.mappers.ModelMapper;
import org.hackreduce.mappers.WikipediaMapper;
import org.hackreduce.mappers.XMLInputFormat;
import org.hackreduce.mappers.XMLRecordReader;
import org.hackreduce.models.WikipediaRecord;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.*;

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

			String wiki = record.getText();
			String text = wikiToText(wiki);
			String[] categories = extractCategories(wiki);
			
			WikipediaData wikiData = new WikipediaData(text, categories);
			
			
			//System.out.println(text);
			//context.getCounter(Count.TOTAL_RECORDS).increment(1);
			//context.write(TOTAL_COUNT, ONE_COUNT);
		}
		
		protected String wikiToText(String wiki) {
			StringWriter writer = new StringWriter();

			HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
			builder.setEmitAsDocument(false);

			MarkupParser parser = new MarkupParser(new MediaWikiLanguage());
			parser.setBuilder(builder);
			parser.parse(wiki);

			final String html = writer.toString();
			final StringBuilder cleaned = new StringBuilder();

			HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
				public void handleText(char[] data, int pos) {
					cleaned.append(new String(data)).append(' ');
				}
			};
			try {
				new ParserDelegator().parse(new StringReader(html), callback, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return cleaned.toString();
		}

		protected String[] extractCategories(String wiki) {
//			System.out.println("TEST MARTIN 1\n\n");
			ArrayList<String> categories = new ArrayList<String>();
//			System.out.println("TEST MARTIN 2\n\n");
			Pattern p = Pattern.compile("\\[\\[Category:([0-9a-zA-z ]+).*\\]\\]");
//			Pattern p = Pattern.compile("(.*Category.*)");
			Matcher m = p.matcher(wiki);
//			System.out.println("TEST MARTIN 3\n\n");
			while(m.find()) {
//				System.out.println("TEST MARTIN 3.5\n\n");
				System.out.println(m.group(1));
				categories.add(m.group(1));
			}
//			System.out.println("TEST MARTIN 4\n\n");
			return categories.toArray(new String[0]);
		}
	}

	@Override
	public void configureJob(Job job) {
		// The Wikipedia format come in XML, so we configure
		// the job to use this format.
		job.setInputFormatClass(XMLInputFormat.class);
		XMLRecordReader.setRecordTags(job, "<page>", "</page>");
	}

	@Override
	public Class<? extends ModelMapper<?, ?, ?, ?, ?>> getMapper() {
		return WikipediaParser.class;
	}

	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new Configuration(), new TaggerMapper(), args);
		System.exit(result);
	}

}
