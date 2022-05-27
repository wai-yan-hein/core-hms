/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.controller;

import java.io.File;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author winswe
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            String path = "config/hibernate.cfg.xml";
            File file = new File(path);
            Configuration configuration = new Configuration().configure(file);
            sessionFactory = configuration.buildSessionFactory();
            //sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
            //SessionFactory sessionFactory1  = new AnnotationConfiguration().setInterceptor(null).configure().buildSessionFactory();
        } catch (HibernateException ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
