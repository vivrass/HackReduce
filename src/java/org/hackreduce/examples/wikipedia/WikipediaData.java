package org.hackreduce.examples.wikipedia;

public class WikipediaData {
	String text;
	String[] categories;
	
	public WikipediaData(String text, String[] categories) {
		this.text = text;
		this.categories = categories;
	}
	
	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public String getText() {
		return text;
	}	
	
	public void setText(String text) {
		this.text = text;
	}
}
