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
    
    private static final String PATH = "/";
    private static final String LOGOUT = "/logout";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String LOGIN_LOGOUT = "/login?logout";
    private static final String LOGIN_ERROR = "/loginError";
    private static final String LOGIN = "/login";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String HTTP_LOCALHOST_4200 = "http://localhost:4200";
    private static final String TRUE = "true";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
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
       http.authorizeRequests().and().formLogin().loginProcessingUrl(LOGIN).defaultSuccessUrl(LOGIN,true).failureForwardUrl(LOGIN_ERROR).permitAll();
       http.authorizeRequests().and().logout().logoutSuccessUrl(LOGIN_LOGOUT).deleteCookies(JSESSIONID).invalidateHttpSession(true).permitAll();
       
       RequestMatcher matcher = new AntPathRequestMatcher(LOGIN);
       DelegatingRequestMatcherHeaderWriter headerWriter = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_ORIGIN,HTTP_LOCALHOST_4200));
       DelegatingRequestMatcherHeaderWriter headerWriterr = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_CREDENTIALS,TRUE));
       
       matcher = new AntPathRequestMatcher(LOGIN_LOGOUT);
       DelegatingRequestMatcherHeaderWriter headerWriterrr = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_ORIGIN,HTTP_LOCALHOST_4200));
       DelegatingRequestMatcherHeaderWriter headerWriterrrr = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_CREDENTIALS,TRUE));
      
       matcher = new AntPathRequestMatcher(LOGOUT);
       DelegatingRequestMatcherHeaderWriter headerLogoutOrigin = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_ORIGIN,HTTP_LOCALHOST_4200));;
       DelegatingRequestMatcherHeaderWriter headerLogoutCred = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_CREDENTIALS,TRUE));
       DelegatingRequestMatcherHeaderWriter headerLogoutHeader = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_HEADERS, "*"));
      
       matcher = new AntPathRequestMatcher(PATH);
       DelegatingRequestMatcherHeaderWriter headerWebService = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_ORIGIN,HTTP_LOCALHOST_4200));
       DelegatingRequestMatcherHeaderWriter headerWebServiceCred = new DelegatingRequestMatcherHeaderWriter(matcher, new StaticHeadersWriter(ACCESS_CONTROL_ALLOW_CREDENTIALS,TRUE));
       
       http.headers().addHeaderWriter(headerWriter).addHeaderWriter(headerWriterr).addHeaderWriter(headerWebService).addHeaderWriter(headerWebServiceCred)
       .addHeaderWriter(headerLogoutOrigin).addHeaderWriter(headerLogoutCred).addHeaderWriter(headerLogoutHeader).addHeaderWriter(headerWriterrrr).addHeaderWriter(headerWriterrr);
      
       http.csrf().disable();
    }
    
    

}
