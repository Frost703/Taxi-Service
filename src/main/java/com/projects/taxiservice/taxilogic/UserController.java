package com.projects.taxiservice.taxilogic;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by O'Neill on 6/8/2017.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public void info(HttpServletRequest req){
        System.out.println(req.getParameter("token"));
    }
}
