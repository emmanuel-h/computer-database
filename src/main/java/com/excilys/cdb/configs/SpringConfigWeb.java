package com.excilys.cdb.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"com.excilys.cdb.controllers.jspControllers", "com.excilys.cdb.daos", "com.excilys.cdb.services"})
public class SpringConfigWeb implements WebMvcConfigurer {

    /**
     * A logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfigWeb.class);

    /**
     * Setup the datasource via Hikari connection pool.
     * @return  The datasource initialized
     */
    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        Properties prop = new Properties();
        try (InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("datasource.properties")) {
            prop.load(input);
            Class.forName("com.mysql.jdbc.Driver");
            HikariConfig hikariConfig = new HikariConfig(prop);
            dataSource = new HikariDataSource(hikariConfig);
            LOGGER.info("Database connected : " + dataSource.getJdbcUrl());
        } catch (IOException e) {
            LOGGER.warn("Error with the property file " + e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Error the class name " + e.getMessage());
        }
        return dataSource;
    }

    /**
     * Setup the view resolver.
     * @return  The view resolver
     */
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/");
    }

    /**
     * Bean to automatically load changes in the source properties file.
     * @return  ReloadableResourceBundleMessageSource
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Write a cookie with the user locale.
     * @return  CookieLocaleResolver
     */
    @Bean
    public CookieLocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        localeResolver.setCookieName("my-locale-cookie");
        localeResolver.setCookieMaxAge(3600);
        return localeResolver;
    }

    /**
     * Allows to change the current locale on every request via a lang request parameter.
     * @return LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeInterceptor());
    }
}
