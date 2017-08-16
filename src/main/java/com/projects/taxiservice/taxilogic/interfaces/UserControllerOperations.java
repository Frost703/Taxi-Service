package com.projects.taxiservice.taxilogic.interfaces;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * Shows all operations that can be performed by UserController
 */
public interface UserControllerOperations {
    ResponseEntity<?> userCabRequest(HttpServletRequest req);
    ResponseEntity<?> getUserHistory(String token);
    ResponseEntity<?> getUserInformation(String token);
    ResponseEntity<?> cancelRequest(String token, int id);
    ResponseEntity<?> leaveFeedback(String token, int id, String feedback);
}
