package com.projects.taxiservice;


import com.projects.taxiservice.DBLogic.DBController;


import com.projects.taxiservice.Users.Driver.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TaxiService {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeans.xml");
        Driver driver = (Driver)context.getBean("driver");
        driver.setLogin("Driver2");
        driver.setName("Name2");
        driver.setPassword("password2");
        Car car = new Car();
        car.setCarClass(CarClass.FAMILYSIZE);
        car.setCarNumber("C2");
        car.setCarDescription("Driver2's car");
        driver.setCar(car);

        DBController.saveToDB(driver);
        driver = new Driver();
        driver = null;
        driver = DBController.getObjectById(Driver.class, 1);
        System.out.println(driver);
    }
}
