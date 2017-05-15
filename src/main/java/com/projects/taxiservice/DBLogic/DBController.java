package com.projects.taxiservice.DBLogic;

import com.projects.taxiservice.TaxiLogic.MyLogger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


/**
 * Created by O'Neill on 7/14/2016.
 */
public final class DBController {
    static SessionFactory factory = null;
    static Session session = null;

    public static boolean saveToDB(Object o){
        if(!(o instanceof DBManageable)) throw new IllegalArgumentException("Object is not manageable for storing to DB: " + o.getClass().getSimpleName());

        factory = new Configuration().configure().buildSessionFactory();
        session = factory.openSession();
        session.beginTransaction();
        session.save(o);
        session.getTransaction().commit();
        session.close();
        factory.close();

        MyLogger.addToLog("Successfully added object to DB: " + o.toString());
        return true;
    }

    public static <T> T getObjectById(Class<T> cl, int id){

        factory = new Configuration().configure().buildSessionFactory();
        session = factory.openSession();
        session.beginTransaction();
        Object o = session.get(cl, id);
        if(!(o.getClass().equals(cl))) throw new IllegalArgumentException("Cannot find an object in DB with class: " + cl.getSimpleName());
        session.close();
        factory.close();

        MyLogger.addToLog("Successfully returned object from DB: " + o.toString());
        return (T)o;
    }

}
