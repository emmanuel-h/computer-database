package com.excilys.cdb.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class Link extends SimpleTagSupport {

    String target;
    String page;
    String limit;
    String search;

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter outJspWriter = getJspContext().getOut();
        StringBuilder stringBuilder = new StringBuilder("?");
        if (null != search) {
            stringBuilder.append("search=" + search + '&');
        }
        stringBuilder.append("page=" + page + "&results=" + limit);
        outJspWriter.println(stringBuilder.toString());
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public void setSearch(String search) {
        this.search = search;
    }

}
