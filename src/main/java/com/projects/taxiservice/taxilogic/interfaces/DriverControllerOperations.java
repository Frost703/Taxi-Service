package com.projects.taxiservice.taxilogic.interfaces;

import org.springframework.http.ResponseEntity;

/**
 * Shows all operations that can be performed by DriverController
 */
public interface DriverControllerOperations {
    ResponseEntity<?> getDriverInformation(String token);
    ResponseEntity<?> changeQueryStatus(String token, String status, int id);
    ResponseEntity<?> getActiveQueries(String token);
    ResponseEntity<?> acceptUserQuery(String token, int id);
    ResponseEntity<?> getActiveQuery(String token);
}
