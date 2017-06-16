package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.dblogic.dao.UserQueryDBController;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Car;
import com.projects.taxiservice.users.query.QueryStatus;
import com.projects.taxiservice.users.query.UserQuery;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * Created by O'Neill on 6/8/2017.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @CrossOrigin
    @RequestMapping(path = "/call", method = RequestMethod.POST)
    public String userCabRequest(HttpServletRequest req){
        String token = req.getParameter("token");
        if(!TokenFilter.isUserSession(token)) return "Token not recognized";

        UserQuery query = new UserQuery();
        query.setAdditionalInformation(req.getParameter("additional"));
        query.setCarClass(Car.getCarClass(req.getParameter("car")));
        query.setAddress(req.getParameter("additional"));
        query.setCustomer(TokenFilter.getUser(token).setActiveQuery(query));
        query.setPhoneNumber(req.getParameter("phone"));
        query.setStatus(QueryStatus.ACTIVE);
        query.setName(req.getParameter("name"));

        try{
            DBController.executeQueryOperation("save", query);
        } catch(SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }

        return "successful";
    }

    @CrossOrigin
    @RequestMapping(path = "/history", method = RequestMethod.GET)
    public Object getUserHistory(HttpServletRequest req){
        String token = req.getParameter("token");
        if(!TokenFilter.isUserSession(token)) return "Token not recognized";

        try {
            return DBController.executeQueryOperation("getUserHistory", new UserQuery().setCustomer(TokenFilter.getUser(token)));
        } catch (SQLException sqe) { return "SQL Exception"; }
    }

    @CrossOrigin
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public Object getUserInformation(HttpServletRequest req){
        String token = req.getParameter("token");
        if(!TokenFilter.isUserSession(token)) return "Token not recognized";

        return TokenFilter.getUser(token).setId(0).setLogin("").setPassword("").setQueries(null).setActiveQuery(null);
    }

    //Not for prototype
//    @CrossOrigin
//    @RequestMapping(path = "/available", method = RequestMethod.GET)
//    public Object getAvailableDrivers(HttpServletRequest req){
//        String token = req.getParameter("token");
//        if(!TokenFilter.isUserSession(token)) return "Token not recognized";
//
//        return "";
//    }

}
