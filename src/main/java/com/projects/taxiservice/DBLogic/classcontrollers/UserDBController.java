package com.projects.taxiservice.dblogic.classcontrollers;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by O'Neill on 5/16/2017.
 */
public class UserDBController {
    private final Connection con;
    private static final Logger logger = Logger.getLogger(UserDBController.class.getName());

    public UserDBController(Connection con) throws IOException, IllegalArgumentException {
        if(con == null) throw new IllegalArgumentException("Connection must be initialized first");
        this.con = con;
        logger.addHandler(DBController.getLogHander());
    }

    public Object execute(String operation, User user){
        Object output;
        switch(operation.toLowerCase()){
            case "register": output = insertUser(user); break;
            case "get": output = selectUser(user); break;
            default: output = null; logger.log(Level.INFO, "Operation not recognized. Returning a null object. Operation: {0}", operation);
        }

        return output;
    }

    public int insertUser(User user) {
        String insertOperation = "INSERT INTO \"users\" " +
                "(login, password, phone, name, address) VALUES " +
                "(?, ?, ?, ?, ?);";

        try {
            if (user == null) throw new IllegalArgumentException("Can't insert a null user to DB");
            String login = user.getLogin(), password = user.getPassword(), name = user.getName();
            String phone = user.getPhone(), address = user.getAddress();

            if (login == null || login.length() < 1) throw new IllegalArgumentException("Login cannot be empty");
            if (password == null || password.length() < 1)
                throw new IllegalArgumentException("Password cannot be empty");
            if (name == null || name.length() < 1) throw new IllegalArgumentException("Name cannot be empty");


            PreparedStatement st = con.prepareStatement(insertOperation);
            st.setString(1, user.getLogin().toLowerCase());
            st.setString(2, user.getPassword());
            st.setString(4, user.getName());

            if (phone == null || phone.length() < 3) st.setString(3, null);
            else st.setString(3, user.getPhone());

            if (address == null || address.length() < 3) st.setString(5, null);
            else st.setString(5, user.getAddress());

            st.execute();

            user = selectUser(user.setId(-1));
            if(user.getId() < 0) throw new SQLException("Failed to insert a user to DB");

            logger.log(Level.INFO, "Inserted a new user to DB with login={0}", user.getLogin());
            return 1;
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return -1;
        } catch(IllegalArgumentException il) {
            logger.log(Level.WARNING, il.getMessage(), il);
            return -1;
        }
    }

    public User selectUser(User user) {
        try {
            if (user == null)
                throw new IllegalArgumentException("Can't perform select statement. Passed User object is null");
            int id = user.getId();
            String login = user.getLogin();

            if (id < 0 && (login == null || login.length() < 1))
                throw new IllegalArgumentException("Can't perform select statement. Id or login must be provided");

            String selectUser = "SELECT * FROM \"users\" WHERE ";
            ResultSet rs = null;
            PreparedStatement st = null;
            if (id > 0) {
                selectUser += "id=?;";
                st = con.prepareStatement(selectUser);
                st.setInt(1, id);

                rs = st.executeQuery();
            } else {
                selectUser += "login=?;";
                st = con.prepareStatement(selectUser);
                st.setString(1, login.toLowerCase());

                rs = st.executeQuery();
            }

            while(rs.next()){
                user.setId(rs.getInt("id")).setLogin(rs.getString("login")).setName(rs.getString("name"))
                        .setPhone(rs.getString("phone")).setAddress(rs.getString("address"));
            }
            rs.close();
            st.close();

        } catch (SQLException sqe) {
            logger.log(Level.WARNING, sqe.getMessage(), sqe);
            return user.setId(-1);
        } catch(IllegalArgumentException il) {
            logger.log(Level.WARNING, il.getMessage(), il);
            return user.setId(-1);
        }

        logger.log(Level.FINEST, "Returned an object from DB with id={0} and login={1}", new Object[] {user.getId(), user.getLogin()});
        return user;
    }


}
