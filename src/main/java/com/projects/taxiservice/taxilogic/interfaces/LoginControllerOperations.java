package com.projects.taxiservice.taxilogic.interfaces;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Shows all operations that can be performed by LoginController
 */
public interface LoginControllerOperations {
    ResponseEntity<?> signIn(HttpServletRequest req);
}
