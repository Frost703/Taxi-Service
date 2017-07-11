package com.projects.taxiservice.taxilogic.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by O'Neill on 7/11/2017.
 */
public interface LoginControllerOperations {
    public String signIn(HttpServletRequest req, HttpServletResponse resp);
}
