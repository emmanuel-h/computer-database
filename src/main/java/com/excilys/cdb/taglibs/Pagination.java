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
    private String search;
    private String todo;

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

            out.write(page == 1
                    ? "<li class=\"disabled\"><a><<</a></li>" + "<li class=\"disabled\"><a><</a></li>"
                    : constructLink(1, "<<") + constructLink(page - 1, "<"));

            for (int i = pgStart; i < pgEnd; i++) {
                out.write(i == page
                        ? "<li class=\"active\"><a>" + page + "</a></li>"
                                : constructLink(i));
            }

            out.write(lastPage
                    ? "<li class=\"disabled\"><a>></a></li>" + "<li class=\"disabled\"><a>>></a></li>"
                    : constructLink(page + 1, ">") + constructLink(totalPages, ">>"));

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
                .append("?");
        if (!"".equals(todo)) {
            link.append("todo=" + todo + '&');
        }
        if (!"".equals(search)) {
            link.append("search=" + search + '&');
        }
        link.append("page=" + page)
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

    public void setSearch(String search) {
        this.search = search;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

}