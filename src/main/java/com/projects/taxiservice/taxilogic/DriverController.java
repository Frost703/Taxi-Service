package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.dblogic.dao.UserQueryDBController;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.users.drivers.Driver;
import com.projects.taxiservice.users.query.QueryStatus;
import com.projects.taxiservice.users.query.UserQuery;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * Created by O'Neill on 6/20/2017.
 */
@RestController
@RequestMapping("/driver")
public class DriverController {

    @CrossOrigin
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public Object getDriverInformation(@RequestParam(value="token") String token){
        if(!TokenFilter.isDriverSession(token)) {
            System.out.println("INFO token not recognized: " + token);
            return "Token not recognized";
        }

        Driver driver = TokenFilter.getDriver(token);

        int orders = 0;
        try {
            orders = UserQueryDBController.getDriverStatistics(driver.getId());
        } catch (SQLException sqe) { sqe.printStackTrace(); }

        return "{\"name\":\""+ driver.getName() +"\", \"orders\":"+ orders +"}";
    }

    @CrossOrigin
    @RequestMapping(path = "/status", method = RequestMethod.POST)
    public Object changeQueryStatus(@RequestParam(value="token") String token, @RequestParam(value="status") String status, @RequestParam(value="id") int id){
        if(!TokenFilter.isDriverSession(token)) {
            System.out.println("INFO token not recognized: " + token);
            return "Token not recognized";
        }

        if(id < 1) return "Not supported";

        Driver driver = TokenFilter.getDriver(token);
        UserQuery queryToChange;
        try {
            queryToChange = UserQueryDBController.selectQuery(id);
        } catch (SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }

        if(queryToChange.getDriver().getId() != driver.getId()) {
            System.out.println("Operation not allowed!");
            return "Not supported";
        }

        QueryStatus activeStatus;
        switch(status){
            case "onroute": activeStatus = QueryStatus.EXECUTING; break;
            case "discard": activeStatus = QueryStatus.DISCARDED; break;
            case "finished": activeStatus = QueryStatus.FINISHED; break;
            default: {
                System.out.println("Status not recognized");
                return "Not supported";
            }
        }

        if(activeStatus == queryToChange.getStatus()) return "Already " + status;

        try {
            UserQueryDBController.updateQueryStatus(queryToChange.getId(), activeStatus);
        } catch (SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }

        return "success";
    }

    @CrossOrigin
    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public Object getActiveQueries(@RequestParam(value="token") String token){
        if(!TokenFilter.isDriverSession(token)) {
            System.out.println("INFO token not recognized: " + token);
            return "Token not recognized";
        }

        try{
            return UserQueryDBController.selectActiveQueries();
        } catch(SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }
    }

    @CrossOrigin
    @RequestMapping(path = "/accept", method = RequestMethod.POST)
    public Object acceptUserQuery(@RequestParam(value="token") String token, @RequestParam(value="id") int id){
        if(!TokenFilter.isDriverSession(token)) {
            System.out.println("INFO token not recognized: " + token);
            return "Token not recognized";
        }

        if(id < 1) return "Not supported";

        Driver driver = TokenFilter.getDriver(token);
        UserQuery queryToChange;
        try {
            queryToChange = UserQueryDBController.selectQuery(id);
        } catch (SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }

        if(queryToChange.getDriver().getId() > 0) {
            System.out.println("DEBUG Already accepted by another driver");
            return "Already accepted by another driver";
        }

        try {
            UserQueryDBController.updateQueryDriver(queryToChange.getId(), driver.getId());
        } catch (SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }

        return "success";
    }

    @CrossOrigin
    @RequestMapping(path = "/active", method = RequestMethod.GET)
    public Object getActiveQuery(@RequestParam(value = "token") String token){
        if(!TokenFilter.isDriverSession(token)) {
            System.out.println("INFO token not recognized: " + token);
            return "Token not recognized";
        }

        Driver driver = TokenFilter.getDriver(token);

        try{
            return UserQueryDBController.selectActiveQuery(driver.getId());
        } catch(SQLException sqe) { sqe.printStackTrace(); return "SQL Exception"; }
    }
}
