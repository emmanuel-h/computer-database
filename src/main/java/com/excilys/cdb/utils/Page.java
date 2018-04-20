package com.excilys.cdb.utils;

import java.util.List;

/**
 * Used for the pagination of the results
 * 
 * @author emmanuelh
 *
 * @param <T>
 */
public class Page<T> {

	/**
	 * The list of results corresponding to the asked page
	 */
	private List<T> results;
	
	/**
	 * Number of results per page
	 */
	private static final int resultsPerPage = 5;
	
	/**
	 * The asked page to display
	 */
	private int currentPage;
	
	/**
	 * The maximum number of pages which can be displayed
	 */
	private int maxResult;

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

	public int getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}
}
