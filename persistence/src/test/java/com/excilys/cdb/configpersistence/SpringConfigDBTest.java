package com.excilys.cdb.configpersistence;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = {"com.excilys.cdb.daos"})
public class SpringConfigDBTest implements WebMvcConfigurer {

    /**
     * Create and configure the session factory.
     * @return  The session factory
     */
    @Bean
    public SessionFactory sessionFactory() {
        return new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
    }
}