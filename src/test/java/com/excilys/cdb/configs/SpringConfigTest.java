package com.excilys.cdb.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan(basePackages = {"com.excilys.cdb.daos", "com.excilys.cdb.services"})
public class SpringConfigTest implements WebMvcConfigurer {

    /**
     * A logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfigTest.class);

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
}