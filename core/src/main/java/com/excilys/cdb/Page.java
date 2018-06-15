package com.excilys.cdb;

import java.util.List;

/**
 * Used for the pagination of the results.
 * @author emmanuelh
 * @param <T>
 */
public class Page<T> {
    
    /**
     * Default constructor
     */
    public Page(){
        
    }
    
    /**
     * Construct a new page with different results. 
     * @param <E>
     * @param page page.    
     */
    public <E> Page(final Page<E> page) {
        this.resultsPerPage = page.getResultsPerPage();
        this.currentPage = page.currentPage;
        this.maxPage = page.maxPage;
    }

    /**
     * The list of results corresponding to the asked page.
     */
    private List<T> results;

    /**
     * Number of results per page.
     */
    private int resultsPerPage = 5;

    /**
     * The asked page to display.
     */
    private int currentPage;

    /**
     * The maximum number of pages which can be displayed.
     */
    private int maxPage;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }
}
