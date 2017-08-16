package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.TaxiService;
import com.projects.taxiservice.model.taxi.CarClass;
import com.projects.taxiservice.persistent.dao.DriverDBController;
import com.projects.taxiservice.persistent.dao.UserDBController;
import com.projects.taxiservice.taxilogic.interfaces.RegisterControllerOperations;
import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.taxi.Car;
import com.projects.taxiservice.model.taxi.Driver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Controls all web requests that come through /register endpoint
 */
@RestController
@CrossOrigin
@RequestMapping("/register")
public class RegisterController implements RegisterControllerOperations {

    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());

    /**
     * Creates a new entity in database according to type of registration (user or driver)
     *
     * @param req with all information from user or driver
     * @return @ref registerUser or @ref registerDriver according to type
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> newAccount(HttpServletRequest req){
        if(req.getParameter("type").equals("user")) return registerUser(req);
        else return registerDriver(req);
    }

    /**
     * Puts user into a database
     *
     * @param req with all information about user
     * @return 400 status when information is missing. 200 status when successful
     */
    private ResponseEntity<?> registerUser(HttpServletRequest req){
        User user = new User();

        user.setLogin(req.getParameter("login")).setPassword(req.getParameter("password"))
                .setName(req.getParameter("name")).setPhone(req.getParameter("phone"))
                .setAddress(req.getParameter("address"));

        try{
            user = UserDBController.insertUser(user);
            if(user.getId() < 1) {
                throw new SQLException("Failed to register new user!");
            }
        }catch(SQLException sqe){
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return new ResponseEntity<>("Fail. Exception: " + sqe.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Object>("success", HttpStatus.OK);
    }

    private ResponseEntity<?> registerDriver(HttpServletRequest req) {
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
        car.setCarClass(CarClass.valueOf(carType));
        driver.setCar(car);

        try{
            driver = DriverDBController.insertDriver(driver);
            if(driver.getId() < 1) throw new SQLException("Failed to register new driver!");
        }catch(SQLException sqe){
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return new ResponseEntity<>("Fail. Exception: " + sqe.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Object>("success", HttpStatus.OK);
    }
}
