package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.TaxiService;
import com.projects.taxiservice.dblogic.dao.UserQueryDBController;
import com.projects.taxiservice.taxilogic.interfaces.UserControllerOperations;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Car;
import com.projects.taxiservice.users.query.QueryStatus;
import com.projects.taxiservice.users.query.UserQuery;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by O'Neill on 6/8/2017.
 */
@RestController
@RequestMapping("/user")
public class UserController implements UserControllerOperations {

    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());
    private final String INVALID_TOKEN = "Token not recognized";

    @CrossOrigin
    @RequestMapping(path = "/call", method = RequestMethod.POST)
    public String userCabRequest(HttpServletRequest req){
        String token = req.getParameter("token");
        if(!isValidUserToken(token)) return INVALID_TOKEN;

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
        if(!isValidUserToken(token)) return INVALID_TOKEN;

        try {
            User user = TokenFilter.getUser(token);
            if(user == User.EMPTY) {
                logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
                return INVALID_TOKEN;
            }

            List queries = UserQueryDBController.getUserHistory(user);
            queries.sort((q1, q2) -> getUserQueryWeight((UserQuery)q2) - getUserQueryWeight((UserQuery)q1));

            return queries;
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Exception"; }
    }

    //Used for sorting. Getting status priorities
    private int getUserQueryWeight(UserQuery query){
        switch(query.getStatus().toString()){
            case "ACTIVE": return 1;
            case "ACCEPTED": return 2;
            case "EXECUTING": return 3;
            default: return 0;
        }
    }

    @CrossOrigin
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public Object getUserInformation(@RequestParam(value="token") String token){
        if(!isValidUserToken(token)) return INVALID_TOKEN;

        User user = TokenFilter.getUser(token);
        return new User().setName(user.getName()).setAddress(user.getAddress()).setPhone(user.getPhone());
    }

    @CrossOrigin
    @RequestMapping(path = "/cancel", method = RequestMethod.POST)
    public String cancelRequest(@RequestParam(value = "token") String token,
                                @RequestParam(value = "id") int id){
        if(id < 1) return "Error: id < 1";
        if(!isValidUserToken(token)) return INVALID_TOKEN;

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
        if(!isValidUserToken(token)) return INVALID_TOKEN;

        try {
            if (UserQueryDBController.updateFeedback(new UserQuery().setId(id), feedback) > 0) return "success";
            else return "failed";
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Error"; }
    }

    private boolean isValidUserToken(String token){
        if(!TokenFilter.isUserSession(token)){
            logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
            return false;
        }

        return true;
    }

}
