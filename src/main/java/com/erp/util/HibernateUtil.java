package com.erp.util;

import com.erp.config.DatabaseConfig;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {
    private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class.getName());
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure()
                    .setProperty("hibernate.connection.url", DatabaseConfig.getUrl())
                    .setProperty("hibernate.connection.username", DatabaseConfig.getUser())
                    .setProperty("hibernate.connection.password", DatabaseConfig.getPassword())
                    .buildSessionFactory();
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, "Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
