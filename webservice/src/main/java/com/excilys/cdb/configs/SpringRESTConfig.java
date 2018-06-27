package com.excilys.cdb.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.excilys.cdb.services.UserService;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@Import(value= SecurityConfig.class)
@ComponentScan(basePackages = {"com.excilys.cdb.configpersistence", "com.excilys.cdb.controllers"})
public class SpringRESTConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
    
      @Autowired
      UserService userService;
    
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
	  
	  @Bean
	  CustomAuthenticationProvider getCustomAuthenticationProvider() {
	      return new CustomAuthenticationProvider();
	  }
}
