package com.projects.taxiservice.persistent.dao;

import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.persistent.DBController;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs database operations on <code>User</code> object
 */
public final class UserDBController {
    private static final Logger logger = Logger.getLogger(UserDBController.class.getName());

    private UserDBController() {}

    /**
     * Inserts <code>User</code> object into database
     *
     * @param user must have login, password, name specified
     * @return a <code>User</code> object with it's index in database
     * @exception IllegalArgumentException if no password or login or name is specified in user
     * @exception SQLException on sql exception
     */
    public static synchronized User insertUser(User user) throws SQLException{
        String insertOperation = "INSERT INTO \"users\" " +
                "(login, password, phone, name, address) VALUES " +
                "(?, ?, ?, ?, ?);";

        if (user == null) {
            logger.log(Level.WARNING, "Passed null User object");
            throw new IllegalArgumentException("Can't insert a null user to DB");
        }

        if (user.getLogin() == null || user.getLogin().length() < 1) {
            logger.log(Level.WARNING, "Passed a User object with empty login");
            throw new IllegalArgumentException("Login cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().length() < 1) {
            logger.log(Level.WARNING, "Passed a User object with empty password");
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (user.getName() == null || user.getName().length() < 1) {
            logger.log(Level.WARNING, "Passed a User object with empty name");
            throw new IllegalArgumentException("User name cannot be empty");
        }

        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(insertOperation, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, user.getLogin().toLowerCase());
            st.setString(2, user.getPassword());
            st.setString(4, user.getName());

            if (user.getPhone() == null || user.getPhone().length() < 3) st.setString(3, null);
            else st.setString(3, user.getPhone());

            if (user.getAddress() == null || user.getAddress().length() < 3) st.setString(5, null);
            else st.setString(5, user.getAddress());

            int result = st.executeUpdate();
            if(result < 1){
                logger.log(Level.WARNING, "Failed to insert a user to DB with login={0}", user.getLogin());
                throw new SQLException("Failed to insert a user to DB");
            }
            else{
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    int userId = rs.getInt(1);
                    if(userId < 1){
                        logger.log(Level.WARNING, "Failed to insert a user to DB with login={0}", user.getLogin());
                        throw new SQLException("Failed to insert a user to DB");
                    }

                    user.setId(userId);
                }
            }
        }

        logger.log(Level.FINEST, "Inserted a new User to DB with id={0}", user.getId());
        return user;
    }

    /**
     * Selects <code>User</code> object from database
     *
     * @param user must have id or login
     * @return a new <code>User</code> object with all information that is stored in database
     * @exception IllegalArgumentException if no id or login is specified in user
     * @exception SQLException on sql exception
     */
    public static synchronized User selectUser(User user) throws SQLException{
        if (user == null) {
            logger.log(Level.WARNING, "Passed null User object");
            throw new IllegalArgumentException("Can't perform select user statement. Passed User object is null");
        }
        if (user.getId() < 0 && (user.getLogin() == null || user.getLogin().length() < 3)) {
            logger.log(Level.WARNING, "Passed a User object with (id<1) and empty login");
            throw new IllegalArgumentException("Can't perform select user statement. Id or login must be provided");
        }

        String selectUser = "SELECT * FROM \"users\" WHERE ";
        boolean idAvailable;

        if (user.getId() > 0) {
            selectUser += "id=?;";
            idAvailable = true;
        }
        else {
            selectUser += "login=?;";
            idAvailable = false;
        }

        User userStored = new User();
        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(selectUser)) {
            if (idAvailable) {
                st.setInt(1, user.getId());
            } else {
                st.setString(1, user.getLogin().toLowerCase());
            }

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                userStored.setId(rs.getInt("id")).setLogin(rs.getString("login")).setName(rs.getString("name"))
                        .setPhone(rs.getString("phone")).setAddress(rs.getString("address"));
            }
        }

        logger.log(Level.FINEST, "Returned an object from DB with id={0}", userStored.getId());
        return userStored;
    }
}
