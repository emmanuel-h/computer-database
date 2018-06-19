package com.excilys.cdb.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
       http.authorizeRequests().and().formLogin().loginPage("/login").loginProcessingUrl("/login").failureForwardUrl("/loginError").permitAll();
       http.authorizeRequests().and().logout().logoutSuccessUrl("/logout").deleteCookies("JSESSIONID").invalidateHttpSession(true).permitAll();
       http.csrf().disable();
    }
    
    

}
