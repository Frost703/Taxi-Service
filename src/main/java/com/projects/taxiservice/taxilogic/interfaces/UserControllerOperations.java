package com.projects.taxiservice.taxilogic.interfaces;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by O'Neill on 7/11/2017.
 */
public interface UserControllerOperations {
    public String userCabRequest(HttpServletRequest req);
    public Object getUserHistory(String token);
    public Object getUserInformation(String token);
    public String cancelRequest(String token, int id);
    public String leaveFeedback(String token, int id, String feedback);
}
