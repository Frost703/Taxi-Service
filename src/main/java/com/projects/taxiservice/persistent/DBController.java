package com.projects.taxiservice.persistent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Configures a DataSource for postgres database. Opens connections to database
 */
public final class DBController {
    private static volatile DataSource source = null;

    private static final Logger logger = Logger.getLogger(DBController.class.getName());
    private static FileHandler dbLogHandler;

    /*
     * Initializes source and adds file handler for logger
     */
    static{
        try{
            dbLogHandler = new FileHandler("dao.log", false);
            logger.addHandler(dbLogHandler);

            source = DataSourceFactory.getDataSource("postgres");
            logger.log(Level.INFO, "Connected to DB.");
        } catch (Exception e) { logger.log(Level.SEVERE, "Failed to load required resources and connect to DB. Error message:\n" + e.toString(), e); }
    }

    /**
     * Gives a new <code>Connection</code> to source
     *
     * @return new Connection opened from source
     * @exception SQLException source can't provide with new connections
     */
    public static Connection getConnection() throws SQLException{
        return source.getConnection();
    }

}
