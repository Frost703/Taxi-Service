package com.projects.taxiservice.dblogic;

import com.projects.taxiservice.dblogic.classcontrollers.CarDBController;
import com.projects.taxiservice.dblogic.classcontrollers.DriverDBController;
import com.projects.taxiservice.dblogic.classcontrollers.UserDBController;
import com.projects.taxiservice.dblogic.classcontrollers.UserQueryDBController;
import com.projects.taxiservice.taxilogic.MyLogger;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Car;
import com.projects.taxiservice.users.drivers.Driver;
import com.projects.taxiservice.users.query.UserQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
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
    private static String postgres;
    private static String username, password;
    private static final String driver = "org.postgresql.Driver";

    private static final Logger logger = Logger.getLogger(DBController.class.getName());
    private static final Properties prop = new Properties();

    private static UserDBController userController = null;
    private static DriverDBController driverController = null;
    private static CarDBController carController = null;
    private static UserQueryDBController queryController = null;

    static{
        try{
            logger.addHandler(new FileHandler("log.log", false));
            prop.load(new FileInputStream(new File("config.config")));

            postgres = prop.getProperty("postgres");
            username = prop.getProperty("user");
            password = prop.getProperty("password");

            Class.forName(driver);

            con = DriverManager.getConnection(postgres, username, password);
            logger.log(Level.INFO, "Connected to DB.");

            userController = new UserDBController(con);
            driverController = new DriverDBController(con);
            carController = new CarDBController(con);
            queryController = new UserQueryDBController(con);
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

    public static synchronized Object executeUserOperation(String operation, User user) throws SQLException, IllegalArgumentException{
        return userController.execute(operation, user);
    }
    public static synchronized Object executeDriverOperation(String operation, Driver driver) throws SQLException, IllegalArgumentException{
        return driverController.execute(operation, driver);
    }
    public static synchronized Object executeCarOperation(String operation, Car car) throws SQLException, IllegalArgumentException{
        return carController.execute(operation, car);
    }
    public static synchronized Object executeQueryOperation(String operation, UserQuery query) throws SQLException, IllegalArgumentException{
        return queryController.execute(operation, query);
    }

    //for operations that don't require an object to be passed as argument (Ex. Select)
    public static synchronized Object executeUserOperation(String operation) throws SQLException, IllegalArgumentException{
        return userController.execute(operation);
    }
    public static synchronized Object executeDriverOperation(String operation) throws SQLException, IllegalArgumentException{
        return driverController.execute(operation);
    }
    public static synchronized Object executeCarOperation(String operation) throws SQLException, IllegalArgumentException{
        return  carController.execute(operation);
    }
    public static synchronized Object executeQueryOperation(String operation) throws SQLException, IllegalArgumentException{
        return queryController.execute(operation);
    }

}
