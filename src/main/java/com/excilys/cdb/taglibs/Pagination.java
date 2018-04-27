package com.excilys.cdb.taglibs;

import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class Pagination extends SimpleTagSupport {
    private String uri;
    private int page;
    private int totalPages;
    private int results;

    /**
     * Constructor initializing the jspWriter.
     * @return  The jspWriter
     */
    private Writer getWriter() {
        JspWriter out = getJspContext().getOut();
        return out;
    }

    @Override
    public void doTag() throws JspException {
        Writer out = getWriter();

        boolean lastPage = page == totalPages;
        int pgStart = Math.max(page - results / 2, 1);
        int pgEnd = pgStart + results;
        if (pgEnd > totalPages + 1) {
            int diff = pgEnd - totalPages;
            pgStart -= diff - 1;
            if (pgStart < 1) {
                pgStart = 1;
            }
            pgEnd = totalPages + 1;
        }

        try {
            out.write("<ul class=\"pagination\">");

            if (page > 1) {
                out.write(constructLink(page - 1, "Previous"));
            }

            for (int i = pgStart; i < pgEnd && i <= 10; i++) {
                if (i == page) {
                    out.write("<li class=\"active\"><a href=\"##\">" + page + "</a></li>");
                } else {
                    out.write(constructLink(i));
                }
            }

            if (!lastPage) {
                out.write(constructLink(page + 1, "Next"));
            }
            out.write("</ul>");

        } catch (java.io.IOException ex) {
            throw new JspException("Error in Paginator tag", ex);
        }
    }

    /**
     * Construct a link with just the page number.
     * @param page  The page number
     * @return      The link built
     */
    private String constructLink(int page) {
        return constructLink(page, String.valueOf(page));
    }

    /**
     * Construct a link with different values.
     * @param page          The page number
     * @param text          The text of the link
     * @return              The link built
     */
    private String constructLink(int page, String text) {
        StringBuilder link = new StringBuilder("<li>")
                .append("<a href=\"")
                .append(uri)
                .append("?page=" + page)
                .append("&results=" + results)
                .append("\">")
                .append(text)
                .append("</a></li>");
        return link.toString();
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setResults(int results) {
        this.results = results;
    }
}