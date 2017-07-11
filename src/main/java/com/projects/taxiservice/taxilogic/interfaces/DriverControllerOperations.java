package com.projects.taxiservice.taxilogic.interfaces;

/**
 * Created by O'Neill on 7/11/2017.
 */
public interface DriverControllerOperations {
    public Object getDriverInformation(String token);
    public Object changeQueryStatus(String token, String status, int id);
    public Object getActiveQueries(String token);
    public Object acceptUserQuery(String token, int id);
    public Object getActiveQuery(String token);
}
