package com.projects.taxiservice;


import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TaxiService {

    public static void main(String[] args) {
        /* ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeans.xml");
        drivers driver = (drivers)context.getBean("driver");
        driver.setLogin("Driver2");
        driver.setName("Name2");
        driver.setPassword("password2");
        Car car = new Car();
        car.setCarClass(CarClass.FAMILYSIZE);
        car.setCarNumber("C2");
        car.setCarDescription("Driver2's car");
        driver.setCar(car);

        DBController.saveToDB(driver);
        driver = new drivers();
        driver = null;
        driver = DBController.getObjectById(drivers.class, 1);
        System.out.println(driver);
        */
        try {
            User user = new User();
            user.setLogin("fourthUser@gmail.com");
            user.setPassword("qwerty");
            user.setPhone("+380966666666");
            user.setName("Fourth User");
            user.setAddress("SomeCity, SomeStreet num, SomeApp num, SomePostalCode");

            System.out.println("Status: " + DBController.executeUserOperation("register", user));

        }
        catch(Exception e) { e.printStackTrace(); }
    }
}
