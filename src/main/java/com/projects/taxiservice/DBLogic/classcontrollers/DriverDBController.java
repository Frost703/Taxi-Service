package com.projects.taxiservice.dblogic.classcontrollers;

import com.projects.taxiservice.users.drivers.Driver;

import java.sql.Connection;

/**
 * Created by O'Neill on 5/16/2017.
 */
public class DriverDBController {
    private final Connection con;

    public DriverDBController(Connection con) {
        this.con = con;
    }

    public Driver execute(String operation){
        switch(operation.toUpperCase()){

        }
        return new Driver();
    }

    public Driver execute(String operation, Driver driver){
        switch(operation.toUpperCase()){

        }
        return driver;
    }

}
