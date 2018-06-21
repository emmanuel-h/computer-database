package com.excilys.cdb.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    CustomAuthenticationProvider customAuthenticationProvider;  
    
    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider) {   
        this.customAuthenticationProvider = customAuthenticationProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override   
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.sessionManagement().maximumSessions(1);
       http.authorizeRequests().and().formLogin().loginProcessingUrl("/login").defaultSuccessUrl("/login",true).failureForwardUrl("/loginError").permitAll();
       http.authorizeRequests().and().logout().logoutSuccessUrl("/login?logout").deleteCookies("JSESSIONID").invalidateHttpSession(true).permitAll();
       
       RequestMatcher matcher = new AntPathRequestMatcher("/login");
       DelegatingRequestMatcherHeaderWriter headerWriter = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Origin","http://localhost:4200"));
       DelegatingRequestMatcherHeaderWriter headerWriterr = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Credentials","true"));
       
       matcher = new AntPathRequestMatcher("/login?logout");
       DelegatingRequestMatcherHeaderWriter headerWriterrr = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Origin","http://localhost:4200"));
       DelegatingRequestMatcherHeaderWriter headerWriterrrr = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Credentials","true"));
      
       matcher = new AntPathRequestMatcher("/logout");
       DelegatingRequestMatcherHeaderWriter headerLogoutOrigin = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Origin","http://localhost:4200"));;
       DelegatingRequestMatcherHeaderWriter headerLogoutCred = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Credentials","true"));
       DelegatingRequestMatcherHeaderWriter headerLogoutHeader = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Headers", "*"));
      
       matcher = new AntPathRequestMatcher("/");
       DelegatingRequestMatcherHeaderWriter headerWebService = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Origin","http://localhost:4200"));
       DelegatingRequestMatcherHeaderWriter headerWebServiceCred = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter("Access-Control-Allow-Credentials","true"));
       
       http.headers().addHeaderWriter(headerWriter).addHeaderWriter(headerWriterr).addHeaderWriter(headerWebService).addHeaderWriter(headerWebServiceCred)
       .addHeaderWriter(headerLogoutOrigin).addHeaderWriter(headerLogoutCred).addHeaderWriter(headerLogoutHeader).addHeaderWriter(headerWriterrrr).addHeaderWriter(headerWriterrr);
      
       http.csrf().disable();
    }
    
    

}
