package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.TaxiService;
import com.projects.taxiservice.dblogic.dao.DriverDBController;
import com.projects.taxiservice.dblogic.dao.UserDBController;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Car;
import com.projects.taxiservice.users.drivers.Driver;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by O'Neill on 5/26/2017.
 */
@RestController
@RequestMapping("/register")
public class RegisterController {

    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public Object newAccount(HttpServletRequest req){
        if(req.getParameter("type").equals("user")) return registerUser(req);
        else return registerDriver(req);
    }

    private String registerUser(HttpServletRequest req){
        User user = new User();

        user.setLogin(req.getParameter("login")).setPassword(req.getParameter("password"))
                .setName(req.getParameter("name")).setPhone(req.getParameter("phone"))
                .setAddress(req.getParameter("address"));

        try{
            user = UserDBController.insertUser(user);
            if(user.getId() < 1) throw new SQLException("Failed to register new user!");
        }catch(SQLException sqe){
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return "Fail. Exception: " + sqe.getMessage();
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
        car.setCarClass(Car.getCarClass(carType));
        driver.setCar(car);

        try{
            driver = DriverDBController.insertDriver(driver);
            if(driver.getId() < 1) throw new SQLException("Failed to register new driver!");
        }catch(SQLException sqe){
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return "Fail. Exception: " + sqe.getMessage();
        }
        return "success";
    }
}
