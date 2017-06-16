package com.projects.taxiservice.dblogic.dao;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.CarClass;
import com.projects.taxiservice.users.drivers.Driver;
import com.projects.taxiservice.users.query.QueryStatus;
import com.projects.taxiservice.users.query.UserQuery;
import org.hibernate.criterion.Subqueries;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by O'Neill on 5/16/2017.
 */
public final class UserQueryDBController {
    private static Connection con;

    private UserQueryDBController() {}

    public static void setConnection(Connection connection) {
        if(connection == null) throw new IllegalArgumentException("Connection object cannot be null!");
        con = connection;
    }

    public static UserQuery execute(String operation, UserQuery userQuery) throws SQLException{
        Object output;
        switch(operation.toUpperCase()){
            case "getUserHistory" : output = getUserHistory(userQuery); break;
            case "save": output = insertQuery(userQuery); break;
            default: throw new IllegalArgumentException("Operation not recognized! Operation: " + operation);
        }
        return userQuery;
    }

    private static List<UserQuery> getUserHistory(UserQuery query) throws SQLException{
        int id = query.getCustomer().getId();
        if(id < 1) throw new IllegalArgumentException("User's id is < 1");

        String selectLastQueries = "SELECT * FROM \"query\" WHERE user=? ORDER BY id DESC LIMIT 4;";
        List<UserQuery> queries = new ArrayList<>(5);

        try(PreparedStatement st = con.prepareStatement(selectLastQueries)){
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                UserQuery selectedQuery = new UserQuery();
                selectedQuery.setName(rs.getString("username"));
                selectedQuery.setPhoneNumber(rs.getString("phone"));
                selectedQuery.setCustomer(query.getCustomer());
                selectedQuery.setCreated(rs.getTimestamp("created").toLocalDateTime());
                selectedQuery.setActivated(rs.getTimestamp("activated").toLocalDateTime());
                selectedQuery.setClosed(rs.getTimestamp("created").toLocalDateTime());
                selectedQuery.setCarClass(CarClass.valueOf(rs.getString("carClass").toUpperCase()));
                selectedQuery.setId(rs.getInt("id"));

                Driver driver = new Driver().setId(rs.getInt("driver"));
                try{
                    driver = (Driver)DBController.executeDriverOperation("get", driver);
                } catch (SQLException sqe) { sqe.printStackTrace(); driver = Driver.EMPTY; }

                selectedQuery.setDriver(driver);
                selectedQuery.setAddress(rs.getString("address"));
                selectedQuery.setAdditionalInformation(rs.getString("additional"));
                selectedQuery.setFeedback(rs.getString("feedback"));
                selectedQuery.setStatus(QueryStatus.valueOf(rs.getString("status").toUpperCase()));

                queries.add(selectedQuery);
            }

            rs.close();
        }
        return queries;
    }

    private static int insertQuery(UserQuery query) throws SQLException{
        if(query == null) throw new IllegalArgumentException("Can't insert a null query.");
        String insertQuery = "INSERT INTO \"query\" " +
                "(activated, additional, address, carclass, closed, created, driver, feedback, phone, status, user, username) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";

        try(PreparedStatement st = con.prepareStatement(insertQuery)){
            st.setTimestamp(1, Timestamp.valueOf(query.getActivated()));
            st.setString(2, query.getAdditionalInformation());
            st.setString(3, query.getAddress());
            st.setString(4, query.getCarClass().toString().toLowerCase());
            st.setTimestamp(5, Timestamp.valueOf(query.getClosed()));
            st.setTimestamp(6, Timestamp.valueOf(query.getCreated()));
            st.setInt(7, query.getDriver().getId());
            st.setString(8, query.getFeedback());
            st.setString(9, query.getPhoneNumber());
            st.setString(10, query.getStatus().toString().toLowerCase());
            st.setInt(11, query.getCustomer().getId());
            st.setString(12, query.getCustomer().getName());

            return st.executeUpdate();
        }
    }
}
