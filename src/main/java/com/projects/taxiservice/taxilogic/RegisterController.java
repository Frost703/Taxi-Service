package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Car;
import com.projects.taxiservice.users.drivers.CarClass;
import com.projects.taxiservice.users.drivers.Driver;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by O'Neill on 5/26/2017.
 */
@RestController
@RequestMapping("/register")
public class RegisterController {
    private long time = System.currentTimeMillis();

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public Object newAccount(HttpServletRequest req){
        if(System.currentTimeMillis() - time < 2*1000) return null;
        time = System.currentTimeMillis();

        if(req.getParameter("type").equals("user")) return registerUser(req);
        else return registerDriver(req);
    }

    private String registerUser(HttpServletRequest req){
        User user = new User();

        user.setLogin(req.getParameter("login")).setPassword(req.getParameter("password"))
                .setName(req.getParameter("name")).setPhone(req.getParameter("phone"))
                .setAddress(req.getParameter("address"));

        try{
            System.out.println("Before user db operation");
            user = (User)DBController.executeUserOperation("register", user);
            System.out.println("After user db operation");
            if(user.getId() < 1) throw new SQLException("Failed to register new user!");
        }catch(SQLException e){
            return "Fail. Exception: " + e.getMessage();
        }

        return "success";
    }

    private String registerDriver(HttpServletRequest req) {
        Driver driver = new Driver();

        driver.setLogin(req.getParameter("login")).setPassword(req.getParameter("password"))
                .setName(req.getParameter("name"))
                .setDrivingSince(LocalDate.parse(
                        req.getParameter("since"), DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                );

        Car car = new Car();
        car.setCarDescription(req.getParameter("description"));
        car.setCarNumber(req.getParameter("plate"));

        String carType = req.getParameter("car");
        switch(carType.trim().toLowerCase()) {
            case "regular":
                car.setCarClass(CarClass.REGULAR);
                break;
            case "family" : car.setCarClass(CarClass.FAMILYSIZE);
                break;
            case "vip" : car.setCarClass(CarClass.VIPCLASS);
                break;
            default : car.setCarClass(CarClass.REGULAR);
        }
        driver.setCar(car);

        try{
            driver = (Driver)DBController.executeDriverOperation("register", driver);
            if(driver.getId() < 1) throw new SQLException("Failed to register new driver!");
        }catch(SQLException e){
            return "Fail. Exception: " + e.getMessage();
        }
        return "success";
    }
}
