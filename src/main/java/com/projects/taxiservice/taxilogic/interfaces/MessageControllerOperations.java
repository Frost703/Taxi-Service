package com.projects.taxiservice.taxilogic.interfaces;

/**
 * Created by O'Neill on 7/11/2017.
 */
public interface MessageControllerOperations {
    public Object sendMessage(String token, String message, int receiver);
    public Object getMessages(String token);
}
