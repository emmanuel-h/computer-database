package com.excilys.cdb.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class Link extends SimpleTagSupport {

    String target;
    String page;
    String limit;

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter outJspWriter = getJspContext().getOut();
        outJspWriter.println(target + "?page=" + page + "&results=" + limit);
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



}
