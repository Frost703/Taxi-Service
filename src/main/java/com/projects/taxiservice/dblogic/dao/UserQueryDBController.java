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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by O'Neill on 5/16/2017.
 */
public final class UserQueryDBController {
    private static Connection con;
    private static final Logger logger = Logger.getLogger(DBController.class.getName());

    static{
        setConnection(DBController.getConnection());
    }

    private UserQueryDBController() {}

    public static synchronized void setConnection(Connection connection) {
        if(connection == null) {
            logger.log(Level.SEVERE, "Passed a null connection to setConnection() method");
            throw new IllegalArgumentException("Connection object cannot be null!");
        }
        con = connection;
    }

    public static synchronized List<UserQuery> getUserHistory(User user) throws SQLException{
        int id = user.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed a UserQuery object with user.id < 1");
            throw new IllegalArgumentException("User's id < 1");
        }

        String selectLastQueries = "SELECT * FROM \"query\" WHERE \"user\"=? ORDER BY id DESC LIMIT 4;";
        List<UserQuery> queries = new ArrayList<>(5);

        try(PreparedStatement st = con.prepareStatement(selectLastQueries)){
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                queries.add(extractUserQuery(rs));
            }

            rs.close();
        }

        logger.log(Level.FINEST, "Returned userHistory collection from DB with size="+queries.size());
        return queries;
    }

    public static synchronized int insertFromUserInput(UserQuery query) throws SQLException{
        if(query == null) {
            logger.log(Level.WARNING, "Passed null UserQuery object");
            throw new IllegalArgumentException("Can't insert a null query.");
        }
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
            st.setString(9, query.getName());

            return st.executeUpdate();
        }
    }

    public static synchronized int insertQuery(UserQuery query) throws SQLException{
        if(query == null) {
            logger.log(Level.WARNING, "Passed null UserQuery object");
            throw new IllegalArgumentException("Can't insert a null query.");
        }
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

    public static synchronized int closeQuery(UserQuery query, QueryStatus status) throws SQLException{
        int id = query.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed a UserQuery object with user.id < 1");
            throw new IllegalArgumentException("query id < 1");
        }

        String update = "UPDATE \"query\" SET status=?, closed=? WHERE id=?";
        try(PreparedStatement st = con.prepareStatement(update)){
            st.setString(1, status.toString().toLowerCase());
            st.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            st.setInt(3, id);

            return st.executeUpdate();
        }
    }

    public static synchronized int updateFeedback(UserQuery query, String feedback) throws SQLException{
        int id = query.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed a UserQuery object with user.id < 1");
            throw new IllegalArgumentException("id < 1");
        }

        String update = "UPDATE \"query\" SET feedback=? WHERE id=?";
        try(PreparedStatement st = con.prepareStatement(update)){
            st.setString(1, feedback);
            st.setInt(2, id);

            return st.executeUpdate();
        }
    }

    public static synchronized int getDriverStatistics(int driverId) throws SQLException{
        if(driverId < 1) {
            logger.log(Level.WARNING, "Passed driverId < 1");
            throw new IllegalArgumentException("id < 1");
        }

        String getTodayOrderCount = "SELECT COUNT(*) FROM \"query\" WHERE \"driver\"=? AND status=?;";
        int result = 0;
        try(PreparedStatement st = con.prepareStatement(getTodayOrderCount)){
            st.setInt(1, driverId);
            st.setString(2, QueryStatus.FINISHED.toString().toLowerCase());

            ResultSet rs = st.executeQuery();
            if(rs.next()){
                result = rs.getInt(1);
            }
            rs.close();
        }
        logger.log(Level.FINEST, "Received a driver statistic with driver id={0} and total finished orders {1}", new Object[]{driverId, result});
        return result;
    }

    public static synchronized UserQuery selectQuery(int id) throws SQLException{
        if(id < 1) throw new IllegalArgumentException("id < 1");

        UserQuery output = UserQuery.EMPTY;
        String select = "SELECT * FROM \"query\" WHERE id=?;";
        try(PreparedStatement st = con.prepareStatement(select)){
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                output = extractUserQuery(rs);
            }
            rs.close();
        }
        return output;
    }

    public static synchronized int updateQueryStatus(UserQuery query, QueryStatus activeStatus) throws SQLException{
        int id = query.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed an UserQuery object with user.id < 1");
            throw new IllegalArgumentException("id < 1");
        }

        String updateStatus = "UPDATE \"query\" SET status=? WHERE id=?;";
        try(PreparedStatement st = con.prepareStatement(updateStatus)){
            st.setString(1, activeStatus.toString().toLowerCase());
            st.setInt(2, id);

            return st.executeUpdate();
        }
    }

    public static synchronized List<UserQuery> selectActiveQueries() throws SQLException{
        String selectActive = "SELECT * FROM \"query\" WHERE status='active'";
        try(PreparedStatement st = con.prepareStatement(selectActive);
            ResultSet rs = st.executeQuery()){

            ArrayList<UserQuery> activeQueries = new ArrayList<>();
            while(rs.next()){
                activeQueries.add(extractUserQuery(rs));
            }

            logger.log(Level.FINEST, "Received a collection with active queries and size={0}", activeQueries.size());
            return activeQueries;
        }
    }

    private static synchronized UserQuery extractUserQuery(ResultSet rs) throws SQLException{
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

        selectedQuery.setCarClass(CarClass.valueOf(rs.getString("carclass").toUpperCase()));
        selectedQuery.setId(rs.getInt("id"));

        Driver driver = new Driver().setId(rs.getInt("driver"));
        try {
            if (driver.getId() > 0) driver.setName(DriverDBController.selectDriver(driver).getName());
        } catch (SQLException sqe) { /*do nothing*/ }

        selectedQuery.setDriver(driver);
        selectedQuery.setAddress(rs.getString("address"));
        selectedQuery.setAdditionalInformation(rs.getString("additional"));
        selectedQuery.setFeedback(rs.getString("feedback"));
        selectedQuery.setStatus(QueryStatus.valueOf(rs.getString("status").toUpperCase()));

        return selectedQuery;
    }

    public static synchronized int updateQueryDriver(UserQuery query, int driver) throws SQLException{
        int id = query.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed a UserQuery object with id < 1");
            throw new IllegalArgumentException("id < 1");
        }
        if(driver < 1) {
            logger.log(Level.WARNING, "Passed a driver id < 1");
            throw new IllegalArgumentException("driver id < 1");
        }

        String updateDriver = "UPDATE \"query\" SET \"driver\"=? WHERE id=?;";
        int result = 1;
        try(PreparedStatement st = con.prepareStatement(updateDriver)){
            st.setInt(1, driver);
            st.setInt(2, id);

            result *= st.executeUpdate();
        }

        return result * updateQueryStatus(query, QueryStatus.ACCEPTED);
    }

    public static synchronized UserQuery selectActiveQuery(int driver) throws SQLException{
        if(driver < 1) throw new IllegalArgumentException("driver id < 1");

        String selectActive = "SELECT * FROM \"query\" WHERE status IN ('accepted', 'executing') AND \"driver\"=?;";
        UserQuery activeQuery = UserQuery.EMPTY;
        try(PreparedStatement st = con.prepareStatement(selectActive)){
            st.setInt(1, driver);
            ResultSet rs = st.executeQuery();

            if(rs.next()){
                activeQuery = extractUserQuery(rs);
            }

            rs.close();

            logger.log(Level.FINEST, "Received a UserQuery object from DB with driver id={0}", driver);
            return activeQuery;
        }
    }
}
