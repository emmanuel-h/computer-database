package com.excilys.cdb.daos;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryManager {

    private SessionFactory sessionFactory;

    public static final SessionFactoryManager INSTANCE = new SessionFactoryManager();

    /**
     * Private constructor initializing the SessionFactory.
     */
    private SessionFactoryManager() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        sessionFactory.close();
    }

}
