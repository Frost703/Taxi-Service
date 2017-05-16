package com.projects.taxiservice.dblogic.classcontrollers;

import com.projects.taxiservice.users.drivers.Car;

import java.sql.Connection;

/**
 * Created by O'Neill on 5/16/2017.
 */
public class CarDBController {
    private final Connection con;

    public CarDBController(Connection con) {
        this.con = con;
    }

    public Car execute(String operation){
        switch(operation.toUpperCase()){

        }
        return new Car();
    }

    public Car execute(String operation, Car car){
        switch(operation.toUpperCase()){

        }
        return car;
    }

}
