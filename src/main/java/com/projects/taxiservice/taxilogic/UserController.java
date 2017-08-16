package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.TaxiService;
import com.projects.taxiservice.model.taxi.CarClass;
import com.projects.taxiservice.persistent.dao.UserQueryDBController;
import com.projects.taxiservice.taxilogic.interfaces.UserControllerOperations;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.queries.QueryStatus;
import com.projects.taxiservice.model.queries.UserQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls all web requests that come through /user endpoint
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController implements UserControllerOperations {

    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());
    private final String INVALID_TOKEN = "Token not recognized";

    /**
     * Saves a UserRequest into a database
     *
     * @param req with all information about user request
     * @return 403 status on invalid token. 500 status on sql exception. 200 status on success
     */
    @RequestMapping(path = "/call", method = RequestMethod.POST)
    public ResponseEntity<?> userCabRequest(HttpServletRequest req){
        String token = req.getParameter("token");
        if(!isValidUserToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        UserQuery query = new UserQuery();
        query.setAdditionalInformation(req.getParameter("info"));
        query.setCarClass(CarClass.valueOf(req.getParameter("car")));
        query.setAddress(req.getParameter("address"));
        query.setCustomer(TokenFilter.getUser(token));
        query.setPhoneNumber(req.getParameter("phone"));
        query.setStatus(QueryStatus.ACTIVE);
        query.setName(req.getParameter("name"));
        query.setCreated(LocalDateTime.now());

        try{
            UserQueryDBController.insertFromUserInput(query);
        } catch(SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Object>("Successfully requested", HttpStatus.OK);
    }

    /**
     * Gets last 4 UserRequest objects of current user from database
     *
     * @param token of current usr
     * @return 403 status when invalid token. 500 status on sql exception. 200 status and list of requests on success
     */
    @RequestMapping(path = "/history", method = RequestMethod.GET)
    public ResponseEntity<?> getUserHistory(@RequestParam(value="token") String token){
        if(!isValidUserToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        try {
            User user = TokenFilter.getUser(token);
            if(user == User.EMPTY) {
                logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
                {
                    return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
                }
            }

            List queries = UserQueryDBController.getUserHistory(user);
            queries.sort((q1, q2) -> QueryStatus.getUserQueryWeight((UserQuery)q2) - QueryStatus.getUserQueryWeight((UserQuery)q1));

            return new ResponseEntity<Object>(queries, HttpStatus.OK);
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    /**
     * Gives a user object with name, address, phone. Information required for creating a new user request
     *
     * @param token of user
     * @return 403 status when invalid token. 200 status and new user object when success
     */
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInformation(@RequestParam(value="token") String token){
        if(!isValidUserToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        User user = TokenFilter.getUser(token);
        return new ResponseEntity<Object>(new User().setName(user.getName()).setAddress(user.getAddress()).setPhone(user.getPhone()), HttpStatus.OK);
    }

    /**
     * Sets a status of UserRequest to CANCELED
     *
     * @param token of user
     * @param id of request
     * @return 403 status when invalid token. 400 status when invalid id.
     *         500 status on sql exception. 200 status when success.
     */
    @RequestMapping(path = "/cancel", method = RequestMethod.POST)
    public ResponseEntity<?> cancelRequest(@RequestParam(value = "token") String token,
                                @RequestParam(value = "id") int id){
        if(id < 1) return new ResponseEntity<Object>("Id < 1", HttpStatus.BAD_REQUEST);
        if(!isValidUserToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        try {
            if (UserQueryDBController.closeQuery(new UserQuery().setId(id), QueryStatus.CANCELLED) > 0) return new ResponseEntity<Object>("Successfully canceled", HttpStatus.OK);
            else return new ResponseEntity<Object>("Failed to cancel request", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    /**
     * Leaves a feedback to specified request
     *
     * @param token of user
     * @param id of request
     * @param feedback to be stored
     * @return 403 status when invalid token. 400 status when invalid id or feedback.
     *         500 status on sql exception. 200 status on success.
     */
    @RequestMapping(path = "/feedback", method = RequestMethod.POST)
    public ResponseEntity<?> leaveFeedback(@RequestParam(value = "token") String token,
                                @RequestParam(value = "id") int id,
                                @RequestParam(value = "feedback") String feedback){
        if(id < 1) {
            logger.log(Level.INFO, "UserQuery id < 1");
            return new ResponseEntity<Object>("Id < 1", HttpStatus.BAD_REQUEST);
        }
        if(feedback.length() < 1) {
            logger.log(Level.INFO, "Passed empty feedback");
            return new ResponseEntity<Object>("Empty feedback", HttpStatus.BAD_REQUEST);
        }
        if(!isValidUserToken(token)) {
        return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        try {
            if (UserQueryDBController.updateFeedback(new UserQuery().setId(id), feedback) > 0) return new ResponseEntity<Object>("Feedback received", HttpStatus.OK);
            else return new ResponseEntity<Object>("Feedback not received", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    /**
     * Checks if user token is in userSessions
     *
     * @param token or user
     * @return true if user is authenticated. Otherwise - false
     */
    private boolean isValidUserToken(String token){
        if(!TokenFilter.isUserSession(token)){
            logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
            return false;
        }

        return true;
    }

}
