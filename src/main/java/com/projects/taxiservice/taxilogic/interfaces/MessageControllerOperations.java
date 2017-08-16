package com.projects.taxiservice.taxilogic.interfaces;

import org.springframework.http.ResponseEntity;

/**
 * Shows all operations that can be performed by MessageController
 */
public interface MessageControllerOperations {
    ResponseEntity<?> sendMessage(String token, String message, int receiver);
    ResponseEntity<?> getMessages(String token);
}
