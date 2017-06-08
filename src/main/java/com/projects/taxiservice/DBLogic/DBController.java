package com.projects.taxiservice.dblogic;

import com.projects.taxiservice.dblogic.dao.DataSourceFactory;
import com.projects.taxiservice.dblogic.dao.DriverDBController;
import com.projects.taxiservice.dblogic.dao.UserDBController;
import com.projects.taxiservice.dblogic.dao.UserQueryDBController;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import com.projects.taxiservice.users.query.UserQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by O'Neill on 7/14/2016.
 */
public final class DBController {
    static SessionFactory factory = null;
    static Session session = null;

    private static volatile Connection con = null;

    private static final Logger logger = Logger.getLogger(DBController.class.getName());
    private static FileHandler dbLogHandler;

    static{
        try{
            dbLogHandler = new FileHandler("log.log", false);
            logger.addHandler(dbLogHandler);

            con = DataSourceFactory.getDataSource("postgres").getConnection();

            logger.log(Level.INFO, "Connected to DB.");

            UserDBController.setConnection(con);
            DriverDBController.setConnection(con);
            UserQueryDBController.setConnection(con);
            logger.log(Level.INFO, "Initialized controller classes for every DB instance.");
        } catch (Exception e) { logger.log(Level.SEVERE, "Failed to load required resources and connect to DB. Error message:\n" + e.toString(), e); }
    }

    public static boolean saveToDB(Object o){
        if(!(o instanceof DBManageable)) throw new IllegalArgumentException("Object is not manageable for storing to DB: " + o.getClass().getSimpleName());

        factory = new Configuration().configure().buildSessionFactory();
        session = factory.openSession();
        session.beginTransaction();
        session.save(o);
        session.getTransaction().commit();
        session.close();
        factory.close();

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

        return (T)o;
    }

    //register (insert), get (select),
    public static synchronized Object executeUserOperation(String operation, User user) throws SQLException{
        return UserDBController.execute(operation, user);
    }
    public static synchronized Object executeDriverOperation(String operation, Driver driver) throws SQLException{
        return DriverDBController.execute(operation, driver);
    }

    public static synchronized Object executeQueryOperation(String operation, UserQuery query) throws SQLException{
        return UserQueryDBController.execute(operation, query);
    }
}
