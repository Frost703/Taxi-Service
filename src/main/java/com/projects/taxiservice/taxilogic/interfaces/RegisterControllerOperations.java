package com.projects.taxiservice.taxilogic.interfaces;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * Shows all operations that can be performed by RegisterController
 */
public interface RegisterControllerOperations {
    ResponseEntity<?> newAccount(HttpServletRequest req);
}
