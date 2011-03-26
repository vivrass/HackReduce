package org.hackreduce.examples.wikipedia;

import java.util.ArrayList;

public class WikipediaData {
	String text;
	ArrayList<String> categories;
	
	public WikipediaData(String text, ArrayList<String> categories) {
		this.text = text;
		this.categories = categories;
	}
	
	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}

	public String getText() {
		return text;
	}	
	
	public void setText(String text) {
		this.text = text;
	}
}
