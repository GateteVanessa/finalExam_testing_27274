package com.auca.library.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Builds and caches a single SessionFactory for the whole application.
 * Tests can point this at a different config file (e.g. an in-memory or
 * throwaway schema) by calling {@link #useConfig(String)} before the first
 * session is requested.
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static String configFile = "hibernate.cfg.xml";

    private HibernateUtil() {
    }

    /**
     * Must be called (if at all) before the first getSessionFactory() call,
     * e.g. from a JUnit @BeforeClass, to point tests at hibernate-test.cfg.xml.
     */
    public static synchronized void useConfig(String cfgFileName) {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
        configFile = cfgFileName;
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration()
                    .configure(configFile)
                    .buildSessionFactory();
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
