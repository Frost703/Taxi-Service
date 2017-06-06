package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import org.springframework.web.bind.annotation.*;

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

        if(type.equals("user")) return loginUser(login, password);
        else return loginDriver(login, password);
    }

    private String loginUser(String login, String password){
        User customer = new User();
        String valid = "valid user", notValid = "wrong user";
        customer.setId(-1)
                .setLogin(login)
                .setPassword(password);

        try {
            User user = (User)DBController.executeUserOperation("get", customer);

            if(user.getId() < 1) return notValid;
            else return valid;
        }catch (SQLException sqe) {
            sqe.printStackTrace();
            return notValid;
        }
    }

    private String loginDriver(String login, String password){
        Driver driver = new Driver();
        String valid = "valid driver", notValid = "wrong driver";
        driver.setId(-1)
                .setLogin(login)
                .setPassword(password);

        try{
            Driver driverStored = (Driver)DBController.executeDriverOperation("get", driver);

            if(driverStored.getId() < 1) return notValid;
            else return valid;
        } catch(SQLException sqe) {
            sqe.printStackTrace();
            return notValid;
        }
    }
}
