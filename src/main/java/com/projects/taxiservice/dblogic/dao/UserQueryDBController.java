package com.projects.taxiservice.dblogic.dao;

import com.projects.taxiservice.users.query.UserQuery;

import java.sql.Connection;

/**
 * Created by O'Neill on 5/16/2017.
 */
public final class UserQueryDBController {
    private static Connection con;

    private UserQueryDBController() {}

    public static void setConnection(Connection connection) {
        if(connection == null) throw new IllegalArgumentException("Connection object cannot be null!");
        con = connection;
    }

    public static UserQuery execute(String operation){
        switch(operation.toUpperCase()){

        }
        return new UserQuery();
    }

    public static UserQuery execute(String operation, UserQuery userQuery){
        switch(operation.toUpperCase()){

        }
        return userQuery;
    }
}
