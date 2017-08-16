package com.projects.taxiservice.persistent;

import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A factory that returns <code>DataSource</code> from some database type
 */
public final class DataSourceFactory {
    private DataSourceFactory() {}

    private static final String DB_PROPERTIES_FILE = "db.properties";
    private static PGPoolingDataSource postgresPoolingDataSource = null;


    /**
     * Provided with <code>DataSource</code> object according to dbtype
     *
     * @param dbtype the name of database provider
     * @return DataSource configured according to dbtype
     * @exception IllegalArgumentException if dbtype parameter is not recognized
     * @exception IOException if properties file is not found
     */
    public static DataSource getDataSource(String dbtype) throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(DB_PROPERTIES_FILE);
        prop.load(fis);

        switch(dbtype){
            case "postgres" : return getPostgresDataSource(prop);
            default : throw new IllegalArgumentException("DB type not recognized!");
        }

    }

    /**
     * Provides <code>DataSource</code> of postgres database
     *
     * @param prop file with all properties required for configuration
     * @return DataSource of postgres database
     * @exception IOException if properties file is not found
     */
    private static DataSource getPostgresDataSource(Properties prop) throws IOException {
        if(postgresPoolingDataSource != null) return postgresPoolingDataSource;

        postgresPoolingDataSource = new PGPoolingDataSource();
        postgresPoolingDataSource.setDataSourceName("Postgres Pooling Data Source");
        postgresPoolingDataSource.setServerName(prop.getProperty("postgresServer"));
        postgresPoolingDataSource.setDatabaseName(prop.getProperty("postgresDriverTable"));
        postgresPoolingDataSource.setPortNumber(Integer.parseInt(prop.getProperty("postgresPort")));
        postgresPoolingDataSource.setUser(prop.getProperty("postgresUser"));
        postgresPoolingDataSource.setPassword(prop.getProperty("postgresPassword"));
        postgresPoolingDataSource.setMaxConnections(20);

        return postgresPoolingDataSource;
}
}
