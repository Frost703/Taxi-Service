package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by O'Neill on 5/24/2017.
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public User login(HttpServletRequest req){
        User customer = new User();
        customer.setLogin(req.getParameter("login")).setPassword(req.getParameter("password"));
        System.out.println(customer.getLogin());
        try {
            User user = (User)DBController.executeUserOperation("get", customer);
            User.setCurrentUser(user);
            if(user.getId() > 0) return customer.setId(user.getId()).setPassword("").setName(user.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }
}
