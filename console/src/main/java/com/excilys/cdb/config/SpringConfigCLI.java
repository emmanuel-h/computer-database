package com.excilys.cdb.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.excilys.cdb.daos", "com.excilys.cdb.services"})
public class SpringConfigCLI {

    /**
     * Create and configure the session factory.
     * @return  The session factory
     */
    @Bean
    public SessionFactory sessionFactory() {
        return new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
    }
}
