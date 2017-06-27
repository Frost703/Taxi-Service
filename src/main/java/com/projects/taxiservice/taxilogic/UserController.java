package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.TaxiService;
import com.projects.taxiservice.dblogic.dao.UserQueryDBController;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Car;
import com.projects.taxiservice.users.query.QueryStatus;
import com.projects.taxiservice.users.query.UserQuery;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by O'Neill on 6/8/2017.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());

    @CrossOrigin
    @RequestMapping(path = "/call", method = RequestMethod.POST)
    public String userCabRequest(HttpServletRequest req){
        String token = req.getParameter("token");
        if(!TokenFilter.isUserSession(token)){
            logger.log(Level.INFO, "token not recognized: " + token);
            return "Token not recognized";
        }

        UserQuery query = new UserQuery();
        query.setAdditionalInformation(req.getParameter("info"));
        query.setCarClass(Car.getCarClass(req.getParameter("car")));
        query.setAddress(req.getParameter("address"));
        query.setCustomer(TokenFilter.getUser(token));
        query.setPhoneNumber(req.getParameter("phone"));
        query.setStatus(QueryStatus.ACTIVE);
        query.setName(req.getParameter("name"));
        query.setCreated(LocalDateTime.now());

        try{
            UserQueryDBController.insertFromUserInput(query);
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Exception"; }

        return "successful";
    }

    @CrossOrigin
    @RequestMapping(path = "/history", method = RequestMethod.GET)
    public Object getUserHistory(@RequestParam(value="token") String token){
        if(!TokenFilter.isUserSession(token)) {
            logger.log(Level.INFO, "token not recognized: " + token);
            return "Token not recognized";
        }

        try {
            User user = TokenFilter.getUser(token);
            if(user == User.EMPTY) {
                logger.log(Level.INFO, "token not recognized: " + token);
                return "Token not recognized";
            }

            UserQuery query = new UserQuery().setCustomer(user);
            return UserQueryDBController.getUserHistory(query);
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Exception"; }
    }

    @CrossOrigin
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public Object getUserInformation(@RequestParam(value="token") String token){
        if(!TokenFilter.isUserSession(token)) {
            logger.log(Level.INFO, "token not recognized: " + token);
            return "Token not recognized";
        }

        User user = TokenFilter.getUser(token);
        return new User().setName(user.getName()).setAddress(user.getAddress()).setPhone(user.getPhone());
    }

    @CrossOrigin
    @RequestMapping(path = "/cancel", method = RequestMethod.POST)
    public String cancelRequest(@RequestParam(value = "token") String token,
                                @RequestParam(value = "id") int id){
        if(id < 1) return "Error: id < 1";
        if(!TokenFilter.isUserSession(token)){
            logger.log(Level.INFO, "token not recognized: " + token);
            return "Token not recognized";
        }

        try {
            if (UserQueryDBController.closeQuery(new UserQuery().setId(id), QueryStatus.CANCELLED) > 0) return "success";
            else return "failed";
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Error"; }
    }

    @CrossOrigin
    @RequestMapping(path = "/feedback", method = RequestMethod.POST)
    public String leaveFeedback(@RequestParam(value = "token") String token,
                                @RequestParam(value = "id") int id,
                                @RequestParam(value = "feedback") String feedback){
        if(id < 1) {
            logger.log(Level.INFO, "UserQuery id < 1");
            return "Error: id < 1";
        }
        if(feedback.length() < 1) {
            logger.log(Level.INFO, "Passed empty feedback");
            return "Error: empty feedback";
        }
        if(!TokenFilter.isUserSession(token)){
            logger.log(Level.INFO, "token not recognized: " + token);
            return "Token not recognized";

        }

        try {
            if (UserQueryDBController.updateFeedback(new UserQuery().setId(id), feedback) > 0) return "success";
            else return "failed";
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Error"; }
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
