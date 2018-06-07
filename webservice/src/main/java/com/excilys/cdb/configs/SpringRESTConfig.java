package com.excilys.cdb.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.excilys.cdb.configpersistence", "com.excilys.cdb.controllers", "com.excilys.cdb.daos", "com.excilys.cdb.services"})
public class SpringRESTConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

	  // Load database and spring security configuration
	  @Override
	  protected Class<?>[] getRootConfigClasses() {
	    return new Class[] { SpringRESTConfig.class };
	  }

	  // Load spring web configuration
	  @Override
	  protected Class<?>[] getServletConfigClasses() {
	    return new Class[] { SpringRESTConfig.class };
	  }

	  @Override
	  protected String[] getServletMappings() {
	    return new String[] { "/" };
	  }
}