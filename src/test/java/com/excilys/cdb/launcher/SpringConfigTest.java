package com.excilys.cdb.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan(basePackages = {"com.excilys.cdb"})
public class SpringConfigTest {

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
}