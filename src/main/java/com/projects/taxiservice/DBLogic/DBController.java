package com.projects.taxiservice.dblogic;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.Connection;
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
        } catch (Exception e) { logger.log(Level.SEVERE, "Failed to load required resources and connect to DB. Error message:\n" + e.toString(), e); }
    }

    public static Connection getConnection(){
        return con;
    }

}
