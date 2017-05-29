package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * Created by O'Neill on 5/24/2017.
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    private long time = System.currentTimeMillis();

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public Object signIn(HttpServletRequest req){
        if(time - System.currentTimeMillis() < 0.5 * 1000) return null;
        time = System.currentTimeMillis();

        if(req.getParameter("type").equals("user")) return loginUser(req);
        else return loginDriver(req);
    }

    private User loginUser(HttpServletRequest req){
        User customer = new User();
        customer.setId(-1).setLogin(req.getParameter("login")).setPassword(req.getParameter("password"));

        try {
            User user = (User)DBController.executeUserOperation("get", customer);

            if(user.getId() > 0) {

                if(!user.getPassword().equals(customer.getPassword())) return customer.setId(-1);

                customer.setId(user.getId()).setPassword("").setName(user.getName());
                User.setCurrentUser(user);
            }
            else customer.setId(-1);
        }catch (SQLException sqe) {
            sqe.printStackTrace();
            customer.setId(-1);
        }

        return customer;
    }

    private Driver loginDriver(HttpServletRequest req){
        Driver driver = new Driver();
        driver.setId(-1).setLogin(req.getParameter("login")).setPassword(req.getParameter("password"));

        try{
            Driver driverStored = (Driver)DBController.executeDriverOperation("get", driver);

            if(driverStored.getId() > 0) {

                if (!driverStored.getPassword().equals(driver.getPassword())) return driver.setId(-1);

                driver.setId(driverStored.getId()).setPassword("").setName(driverStored.getName());
                Driver.setCurrentDriver(driverStored);
            }
            else driver.setId(-1);
        } catch(SQLException sqe) {
            sqe.printStackTrace();
            driver.setId(-1);
        }

        return driver;
    }
}
