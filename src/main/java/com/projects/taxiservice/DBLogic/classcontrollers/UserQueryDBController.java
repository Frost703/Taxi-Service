package com.projects.taxiservice.dblogic.classcontrollers;

import com.projects.taxiservice.users.query.UserQuery;

import java.sql.Connection;

/**
 * Created by O'Neill on 5/16/2017.
 */
public class UserQueryDBController {
    private final Connection con;

    public UserQueryDBController(Connection con) {
        this.con = con;
    }

    public UserQuery execute(String operation){
        switch(operation.toUpperCase()){

        }
        return new UserQuery();
    }

    public UserQuery execute(String operation, UserQuery userQuery){
        switch(operation.toUpperCase()){

        }
        return userQuery;
    }
}
