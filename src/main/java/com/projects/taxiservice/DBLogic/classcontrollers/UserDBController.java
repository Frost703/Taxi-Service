package com.projects.taxiservice.dblogic.classcontrollers;

import com.projects.taxiservice.users.customer.User;

import java.sql.Connection;

/**
 * Created by O'Neill on 5/16/2017.
 */
public class UserDBController {
    private final Connection con;

    public UserDBController(Connection con) {
        this.con = con;
    }

    public Object execute(String operation){
        switch(operation.toUpperCase()){

        }
        return new User();
    }

    public Object execute(String operation, User user){
        switch(operation.toUpperCase()){

        }
        return user;
    }


}
