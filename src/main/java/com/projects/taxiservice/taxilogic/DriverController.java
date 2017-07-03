package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.TaxiService;
import com.projects.taxiservice.dblogic.dao.UserQueryDBController;
import com.projects.taxiservice.taxilogic.utilities.DirectMessenger;
import com.projects.taxiservice.taxilogic.utilities.MessageStyler;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import com.projects.taxiservice.users.query.QueryStatus;
import com.projects.taxiservice.users.query.UserQuery;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by O'Neill on 6/20/2017.
 */
@RestController
@RequestMapping("/driver")
public class DriverController {

    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());
    private final String INVALID_TOKEN = "Token not recognized";

    @CrossOrigin
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public Object getDriverInformation(@RequestParam(value="token") String token){
        if(!TokenFilter.isDriverSession(token)) {
            logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
            return INVALID_TOKEN;
        }

        Driver driver = TokenFilter.getDriver(token);

        int orders = 0;
        try {
            orders = UserQueryDBController.getDriverStatistics(driver.getId());
        } catch (SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); }

        return "{\"name\":\""+ driver.getName() +"\", \"orders\":"+ orders +"}";
    }

    @CrossOrigin
    @RequestMapping(path = "/status", method = RequestMethod.POST)
    public Object changeQueryStatus(@RequestParam(value="token") String token,
                                    @RequestParam(value="status") String status,
                                    @RequestParam(value="id") int id){
        if(!TokenFilter.isDriverSession(token)) {
            logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
            return INVALID_TOKEN;
        }

        if(id < 1) {
            logger.log(Level.INFO, "operation not supported. id=" + id);
            return "Not supported";
        }

        Driver driver = TokenFilter.getDriver(token);
        UserQuery queryToChange;
        try {
            queryToChange = UserQueryDBController.selectQuery(id);
        } catch (SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }

        if(queryToChange.getDriver().getId() != driver.getId()) {
            logger.log(Level.INFO, "operation not supported. driverId != current driver");
            return "Not supported";
        }

        QueryStatus activeStatus;
        switch(status){
            case "onroute": activeStatus = QueryStatus.EXECUTING; break;
            case "discard": activeStatus = QueryStatus.DISCARDED; break;
            case "finished": activeStatus = QueryStatus.FINISHED; break;
            default: {
                logger.log(Level.INFO, "status not recognized. status=" + status);
                return "Not supported";
            }
        }

        if(activeStatus == queryToChange.getStatus()) {
            logger.log(Level.INFO, "status already changed. status=" + status);
            return "Already " + status;
        }

        try {
            UserQueryDBController.updateQueryStatus(queryToChange, activeStatus);
        } catch (SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Exception"; }

        return "success";
    }

    @CrossOrigin
    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public Object getActiveQueries(@RequestParam(value="token") String token){
        if(!TokenFilter.isDriverSession(token)) {
            logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
            return INVALID_TOKEN;
        }

        try{
            return UserQueryDBController.selectActiveQueries();
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Exception"; }
    }

    @CrossOrigin
    @RequestMapping(path = "/accept", method = RequestMethod.POST)
    public Object acceptUserQuery(@RequestParam(value="token") String token, @RequestParam(value="id") int id){
        if(!TokenFilter.isDriverSession(token)) {
            logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
            return INVALID_TOKEN;
        }

        if(id < 1) {
            logger.log(Level.INFO, "operation not supported. id=" + id);
            return "Not supported";
        }

        Driver driver = TokenFilter.getDriver(token);
        UserQuery queryToChange;
        try {
            queryToChange = UserQueryDBController.selectQuery(id);
        } catch (SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }

        if(queryToChange.getDriver().getId() > 0) {
            logger.log(Level.INFO, "Query already accepted by another driver");
            return "Already accepted by another driver";
        }

        try {
            UserQueryDBController.updateQueryDriver(queryToChange, driver.getId());
        } catch (SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Exception"; }

        return "success";
    }

    @CrossOrigin
    @RequestMapping(path = "/active", method = RequestMethod.GET)
    public Object getActiveQuery(@RequestParam(value = "token") String token){
        if(!TokenFilter.isDriverSession(token)) {
            logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
            return INVALID_TOKEN;
        }

        Driver driver = TokenFilter.getDriver(token);

        try{
            return UserQueryDBController.selectActiveQuery(driver.getId());
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return "SQL Exception"; }
    }
}
