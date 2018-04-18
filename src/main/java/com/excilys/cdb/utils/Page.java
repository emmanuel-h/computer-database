package main.java.com.excilys.cdb.utils;

import java.util.List;

public class Page<T> {

	private List<T> results;
	private static final int resultsPerPage = 5;
	private int currentPage;
	
	public List<T> getResults() {
		return results;
	}
	
	public void setResults(List<T> results) {
		this.results = results;
	}
	
	public int getResultsPerPage() {
		return resultsPerPage;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
