package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.taxilogic.utilities.RandomTokenGen;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public String signIn(HttpServletRequest req, HttpServletResponse resp){
        if(System.currentTimeMillis() - time < 0.5 * 1000) {
            return "Requests are too frequent. Pls wait 1 second";
        }
        time = System.currentTimeMillis();

        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String type = req.getParameter("type");

        //add a new user session and valid response
        if(type.equals("user")) {
            User user = loginUser(login, password);
            if(user.getId() > 0) {
                String secureToken = RandomTokenGen.getSecureToken();
//                Cookie token = new Cookie("token", secureToken);
//                token.setMaxAge(60*60*5);
//                resp.addCookie(token);
//                System.out.println("Token for current user is: " + secureToken);
                //TokenFilter.removeUserSession(userSessionKey);
                //TokenFilter.addUserSession(userSessionKey, user);

                return secureToken;
            }
            else return "wrong user";
        }
        //add a new driver session and valid response
        else {
            Driver driver = loginDriver(login, password);
            if(driver.getId() > 0) {
//                String driverSessionKey = req.getSession().getId();
//                System.out.println(driverSessionKey);
                //TokenFilter.removeDriverSession(driverSessionKey);
                //TokenFilter.addDriverSession(driverSessionKey, driver);

                return "valid driver";
            }
            else return "wrong driver";
        }
    }

    private User loginUser(String login, String password){
        User customer = new User();
        customer.setId(-1)
                .setLogin(login)
                .setPassword(password);

        try {
            User user = (User)DBController.executeUserOperation("get", customer);

            if(user.getId() < 1) return User.EMPTY;
            else{
                return user;
            }
        }catch (SQLException sqe) {
            sqe.printStackTrace();
            return User.EMPTY;
        }
    }

    private Driver loginDriver(String login, String password){
        Driver driver = new Driver();
        driver.setId(-1)
                .setLogin(login)
                .setPassword(password);

        try{
            Driver driverStored = (Driver)DBController.executeDriverOperation("get", driver);

            if(driverStored.getId() < 1) return Driver.EMPTY;
            else return driverStored;
        } catch(SQLException sqe) {
            sqe.printStackTrace();
            return Driver.EMPTY;
        }
    }
}
