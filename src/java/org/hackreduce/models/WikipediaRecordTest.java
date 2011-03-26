package org.hackreduce.models;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.w3c.dom.bootstrap.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import junit.framework.*;



public class WikipediaRecordTest extends TestCase {
	private WikipediaRecord record;
	private String xml;
	public WikipediaRecordTest() throws Exception {
	  File file = new File("datasets/wikipedia/wikipedia-sample.xml");
	  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	  DocumentBuilder db = dbf.newDocumentBuilder();
	  Document doc = db.parse(file);
	  doc.getDocumentElement().normalize();
	  NodeList nodeLst = doc.getElementsByTagName("page");
	  Node n = nodeLst.item(1);
	  
	  DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
	  DOMImplementationLS lsImpl = 
	    (DOMImplementationLS)registry.getDOMImplementation("LS");
	  LSSerializer serializer = lsImpl.createLSSerializer();
	  xml = serializer.writeToString(n);
	}
	
	public void setUp() {
		record = new WikipediaRecord(xml);
	}
	
	public void testGetRawText() {
		System.out.println(record.getRawText());
	}
	
	public void testGetCategories() {
		ArrayList<String> categories = record.getCategories();
		assertEquals(4, categories.size());
	}
}
