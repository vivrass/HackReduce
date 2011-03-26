package org.hackreduce.examples.wikipedia;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Reducer;
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
			ArrayList<String> categories = new ArrayList<String>();
			Pattern p = Pattern.compile("\\[\\[Category:([0-9a-zA-z ]+).*\\]\\]");
			Matcher m = p.matcher(wiki);
			while(m.find()) {
				System.out.println(m.group(1));
				categories.add(m.group(1));
			}
			return categories.toArray(new String[0]);
		}
	}

}
