package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.TaxiService;
import com.projects.taxiservice.persistent.dao.UserQueryDBController;
import com.projects.taxiservice.taxilogic.interfaces.DriverControllerOperations;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.model.taxi.Driver;
import com.projects.taxiservice.model.queries.QueryStatus;
import com.projects.taxiservice.model.queries.UserQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls all web requests that come through /driver endpoint
 */
@RestController
@CrossOrigin
@RequestMapping("/driver")
public class DriverController implements DriverControllerOperations {

    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());
    private final String INVALID_TOKEN = "Token not recognized";

    /**
     * Finds all finished requests by current driver to display on ui
     *
     * @param token of current driver
     * @return 403 status when invalid token. 400 status when token is invalid.
     *         200 status, name and amount of orders when successful
     */
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public ResponseEntity<?> getDriverInformation(@RequestParam(value="token") String token){
        if(!isValidDriverToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        Driver driver = TokenFilter.getDriver(token);

        int orders = 0;
        try {
            orders = UserQueryDBController.getDriverStatistics(driver.getId());
        } catch (SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); }

        return new ResponseEntity<Object>("{\"name\":\""+ driver.getName() +"\", \"orders\":"+ orders +"}", HttpStatus.OK);
    }

    /**
     * Changes the status of specified query
     *
     * @param token of current driver
     * @param status to be displayed
     * @param id of query to be changed
     * @return 403 status when invalid token. 400 status when id, status is invalid. 422 when status was already changed.
     *         500 status on sql exception. 200 status when successful
     */
    @RequestMapping(path = "/status", method = RequestMethod.POST)
    public ResponseEntity<?> changeQueryStatus(@RequestParam(value="token") String token,
                                    @RequestParam(value="status") String status,
                                    @RequestParam(value="id") int id){
        if(!isValidDriverToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        if(id < 1) {
            logger.log(Level.INFO, "operation not supported. id=" + id);
            return new ResponseEntity<Object>("Operation not supported. id<1", HttpStatus.BAD_REQUEST);
        }

        Driver driver = TokenFilter.getDriver(token);
        UserQuery queryToChange;
        try {
            queryToChange = UserQueryDBController.selectQuery(id);
        } catch (SQLException sqe) { sqe.printStackTrace(); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }

        if(queryToChange.getDriver().getId() != driver.getId()) {
            logger.log(Level.INFO, "operation not supported. driverId != current driver");
            return new ResponseEntity<Object>("Operation Forbidden!", HttpStatus.FORBIDDEN);
        }

        QueryStatus activeStatus;
        switch(status){
            case "onroute": activeStatus = QueryStatus.EXECUTING; break;
            case "discard": activeStatus = QueryStatus.DISCARDED; break;
            case "finished": activeStatus = QueryStatus.FINISHED; break;
            default: {
                logger.log(Level.INFO, "status not recognized. status=" + status);
                return new ResponseEntity<Object>("Operation not supported. Status not recognized", HttpStatus.BAD_REQUEST);
            }
        }

        if(activeStatus == queryToChange.getStatus()) {
            logger.log(Level.INFO, "status already changed. status=" + status);
            return new ResponseEntity<Object>("Status already changed", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        try {
            UserQueryDBController.updateQueryStatus(queryToChange, activeStatus);
        } catch (SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }

        return new ResponseEntity<Object>("Status changed", HttpStatus.OK);
    }

    /**
     * Returns a list of UserQuery objects with status=active
     *
     * @param token of requester
     * @return 403 status when invalid token. 400 status when token is invalid. 500 status on sql exception.
     *         200 status and list of active queries when successful
     */
    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public ResponseEntity<?> getActiveQueries(@RequestParam(value="token") String token){
        if(!isValidDriverToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        try{
            return new ResponseEntity<Object>(UserQueryDBController.selectActiveQueries(), HttpStatus.OK);
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    /**
     * Changes driver for specified query and sets status as ACCEPTED
     *
     * @param token of driver
     * @param id of request
     * @return 403 status when invalid token. 400 status when token, id is invalid. 422 when another driver accepted this query.
     *         500 status on sql exception. 200 status when successful
     */
    @RequestMapping(path = "/accept", method = RequestMethod.POST)
    public ResponseEntity<?> acceptUserQuery(@RequestParam(value="token") String token, @RequestParam(value="id") int id){
        if(!isValidDriverToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        if(id < 1) {
            logger.log(Level.INFO, "operation not supported. id=" + id);
            return new ResponseEntity<Object>("Operation not supported. id<1", HttpStatus.BAD_REQUEST);
        }

        Driver driver = TokenFilter.getDriver(token);
        UserQuery queryToChange;
        try {
            queryToChange = UserQueryDBController.selectQuery(id);
        } catch (SQLException sqe) { sqe.printStackTrace(); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }

        if(queryToChange.getDriver().getId() > 0) {
            logger.log(Level.INFO, "Query already accepted by another driver");
            return new ResponseEntity<Object>("Already accepted by another driver", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        try {
            UserQueryDBController.updateQueryDriver(queryToChange, driver.getId());
        } catch (SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }

        return new ResponseEntity<Object>("Accepted query", HttpStatus.OK);
    }

    /**
     * Gets a query with status=(ACTIVE or ACCEPTED) of specified driver
     *
     * @param token of the driver
     * @return 403 status when invalid token. 400 status when token is invalid.
     *         500 status on sql exception. 200 status and UserQuery when successful
     */
    @RequestMapping(path = "/active", method = RequestMethod.GET)
    public ResponseEntity<?> getActiveQuery(@RequestParam(value = "token") String token){
        if(!isValidDriverToken(token)) {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }

        Driver driver = TokenFilter.getDriver(token);

        try{
            return new ResponseEntity<Object>(UserQueryDBController.selectActiveQuery(driver.getId()), HttpStatus.OK);
        } catch(SQLException sqe) { logger.log(Level.WARNING, sqe.getMessage(), sqe); return new ResponseEntity<Object>("Sql exception", HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    /**
     * Checks if driver token is in driverSessions
     *
     * @param token or driver
     * @return true if driver is authenticated. Otherwise - false
     */
    private boolean isValidDriverToken(String token){
        if(!TokenFilter.isDriverSession(token)){
            logger.log(Level.INFO, INVALID_TOKEN + ": " + token);
            return false;
        }

        return true;
    }
}
