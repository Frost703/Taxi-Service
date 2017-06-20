package com.projects.taxiservice.dblogic.dao;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.CarClass;
import com.projects.taxiservice.users.drivers.Driver;
import com.projects.taxiservice.users.query.QueryStatus;
import com.projects.taxiservice.users.query.UserQuery;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by O'Neill on 5/16/2017.
 */
public final class UserQueryDBController {
    private static Connection con;

    static{
        setConnection(DBController.getConnection());
    }

    private UserQueryDBController() {}

    public static void setConnection(Connection connection) {
        if(connection == null) throw new IllegalArgumentException("Connection object cannot be null!");
        con = connection;
    }

    public static List<UserQuery> getUserHistory(UserQuery query) throws SQLException{
        int id = query.getCustomer().getId();
        if(id < 1) throw new IllegalArgumentException("User's id is < 1");

        String selectLastQueries = "SELECT * FROM \"query\" WHERE \"user\"=? ORDER BY id DESC LIMIT 4;";
        List<UserQuery> queries = new ArrayList<>(5);

        try(PreparedStatement st = con.prepareStatement(selectLastQueries)){
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                UserQuery selectedQuery = new UserQuery();
                selectedQuery.setId(rs.getInt("id"));
                selectedQuery.setName(rs.getString("username"));
                selectedQuery.setPhoneNumber(rs.getString("phone"));
                selectedQuery.setCustomer(new User().setId(rs.getInt("user")));
                selectedQuery.setCreated(rs.getTimestamp("created").toLocalDateTime());

                Timestamp timeActivated = rs.getTimestamp("activated");
                if(timeActivated != null)
                    selectedQuery.setActivated(timeActivated.toLocalDateTime());

                Timestamp timeClosed = rs.getTimestamp("created");
                if(timeClosed != null)
                    selectedQuery.setClosed(timeClosed.toLocalDateTime());

                selectedQuery.setCarClass(CarClass.valueOf(rs.getString("carClass").toUpperCase()));
                selectedQuery.setId(rs.getInt("id"));

                Driver driver = new Driver().setId(rs.getInt("driver"));

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

    public static int insertFromUserInput(UserQuery query) throws SQLException{
        if(query == null) throw new IllegalArgumentException("Can't insert a null query.");
        String insertQuery = "INSERT INTO \"query\" " +
                "(additional, address, carclass, created, feedback, phone, status, \"user\", username) " +
                "VALUES (?,?,?,?,?,?,?,?,?);";

        try(PreparedStatement st = con.prepareStatement(insertQuery)){
            st.setString(1, query.getAdditionalInformation());
            st.setString(2, query.getAddress());
            st.setString(3, query.getCarClass().toString().toLowerCase());
            st.setTimestamp(4, Timestamp.valueOf(query.getCreated()));
            st.setString(5, query.getFeedback());
            st.setString(6, query.getPhoneNumber());
            st.setString(7, query.getStatus().toString().toLowerCase());
            st.setInt(8, query.getCustomer().getId());
            st.setString(9, query.getCustomer().getName());

            return st.executeUpdate();
        }
    }

    public static int insertQuery(UserQuery query) throws SQLException{
        if(query == null) throw new IllegalArgumentException("Can't insert a null query.");
        String insertQuery = "INSERT INTO \"query\" " +
                "(activated, additional, address, carclass, closed, created, \"driver\", feedback, phone, status, \"user\", username) " +
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

    public static int closeQuery(UserQuery query, QueryStatus status) throws SQLException{
        int id = query.getId();
        if(id < 1) throw new IllegalArgumentException("id < 1");

        String update = "UPDATE \"query\" SET status=?, closed=? WHERE id=?";
        try(PreparedStatement st = con.prepareStatement(update)){
            st.setString(1, status.toString().toLowerCase());
            st.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            st.setInt(3, id);

            return st.executeUpdate();
        }
    }

    public static int updateFeedback(UserQuery query, String feedback) throws SQLException{
        int id = query.getId();
        if(id < 1) throw new IllegalArgumentException("id < 1");

        String update = "UPDATE \"query\" SET feedback=? WHERE id=?";
        try(PreparedStatement st = con.prepareStatement(update)){
            st.setString(1, feedback);
            st.setInt(2, id);

            return st.executeUpdate();
        }
    }
}
