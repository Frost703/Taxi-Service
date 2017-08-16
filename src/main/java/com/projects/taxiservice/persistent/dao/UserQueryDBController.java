package com.projects.taxiservice.persistent.dao;

import com.projects.taxiservice.persistent.DBController;
import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.taxi.CarClass;
import com.projects.taxiservice.model.taxi.Driver;
import com.projects.taxiservice.model.queries.QueryStatus;
import com.projects.taxiservice.model.queries.UserQuery;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs database operations on <code>UserQuery</code> object
 */
public final class UserQueryDBController {
    private static final Logger logger = Logger.getLogger(UserQueryDBController.class.getName());

    private UserQueryDBController() {}

    /**
     * Selects a list of <code>UserQuery</code> objects from database
     * <P><B>Note:</B> This method returns only 4 last <code>UserQuery</code> objects submitted by current user</P>
     *
     * @param user must have id
     * @return a new <code>List<UserQuery></code> object with 4 last queries made by current user
     * @exception IllegalArgumentException if no id is specified in user
     * @exception SQLException on sql exception
     */
    public static synchronized List<UserQuery> getUserHistory(User user) throws SQLException{
        int id = user.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed a UserQuery object with user.id < 1");
            throw new IllegalArgumentException("User's id < 1");
        }

        String selectLastQueries = "SELECT * FROM \"queries\" WHERE \"user\"=? ORDER BY id DESC LIMIT 4;";
        List<UserQuery> queries = new ArrayList<>(5);

        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(selectLastQueries)){
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                queries.add(extractUserQuery(rs));
            }
        }

        logger.log(Level.FINEST, "Returned userHistory collection from DB with size="+queries.size());
        return queries;
    }

    /**
     * Inserts <code>UserQuery</code> object into database
     *
     * @param query that will be saved
     * @return a result of current operation (amount of fields changed/added into database)
     * @exception IllegalArgumentException if query is null
     * @exception SQLException on sql exception
     */
    public static synchronized int insertFromUserInput(UserQuery query) throws SQLException{
        if(query == null) {
            logger.log(Level.WARNING, "Passed null UserQuery object");
            throw new IllegalArgumentException("Can't insert a null queries.");
        }
        String insertQuery = "INSERT INTO \"queries\" " +
                "(additional, address, carclass, created, feedback, phone, status, \"user\", username) " +
                "VALUES (?,?,?,?,?,?,?,?,?);";

        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(insertQuery)){
            st.setString(1, query.getAdditionalInformation());
            st.setString(2, query.getAddress());
            st.setString(3, query.getCarClass().toString().toLowerCase());
            st.setTimestamp(4, Timestamp.valueOf(query.getCreated()));
            st.setString(5, query.getFeedback());
            st.setString(6, query.getPhoneNumber());
            st.setString(7, query.getStatus().toString().toLowerCase());
            st.setInt(8, query.getCustomer().getId());
            st.setString(9, query.getName());

            int result = st.executeUpdate();
            if(result < 1){
                logger.log(Level.WARNING, "Failed to insert a new UserQuery to DB. Customer login={0}", query.getCustomer().getLogin());
                throw new SQLException("Failed to insert a new UserQuery to DB");
            }

            return result;
        }
    }

    /**
     * Changes <code>QueryStatus</code> of current <code>UserQuery</code> object in database
     *
     * @param query query to be changed
     * @param status that query will have
     * @return a result of current operation (amount of fields changed/added into database)
     * @exception IllegalArgumentException if no id < 1 in query
     * @exception SQLException on sql exception
     */
    public static synchronized int closeQuery(UserQuery query, QueryStatus status) throws SQLException{
        int id = query.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed a UserQuery object with user.id < 1");
            throw new IllegalArgumentException("queries id < 1");
        }

        String update = "UPDATE \"queries\" SET status=?, closed=? WHERE id=?";
        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(update)){
            st.setString(1, status.toString().toLowerCase());
            st.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            st.setInt(3, id);

            int result = st.executeUpdate();
            if(result < 1){
                logger.log(Level.WARNING, "Failed to insert a new UserQuery to DB. Customer login={0}", query.getCustomer().getLogin());
                throw new SQLException("Failed to insert a new UserQuery to DB");
            }

            return result;
        }
    }

    /**
     * Changes feedback field of current <code>UserQuery</code> object in database
     *
     * @param query query to be changed
     * @param feedback left by user
     * @return a result of current operation (amount of fields changed/added into database)
     * @exception IllegalArgumentException if no id < 1 in query
     * @exception SQLException on sql exception
     */
    public static synchronized int updateFeedback(UserQuery query, String feedback) throws SQLException{
        int id = query.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed a UserQuery object with user.id < 1");
            throw new IllegalArgumentException("id < 1");
        }

        String update = "UPDATE \"queries\" SET feedback=? WHERE id=?";
        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(update)){
            st.setString(1, feedback);
            st.setInt(2, id);

            int result = st.executeUpdate();
            if(result < 1){
                logger.log(Level.WARNING, "Failed to insert a new UserQuery to DB. Customer login={0}", query.getCustomer().getLogin());
                throw new SQLException("Failed to insert a new UserQuery to DB");
            }

            return result;
        }
    }

    /**
     * Gets amount of <code>UserQuery</code> objects of specified driver with status=finished
     *
     * @param driverId id of the driver
     * @return amount of queries finished by driver
     * @exception IllegalArgumentException if driverId < 1
     * @exception SQLException on sql exception
     */
    public static synchronized int getDriverStatistics(int driverId) throws SQLException{
        if(driverId < 1) {
            logger.log(Level.WARNING, "Passed driverId < 1");
            throw new IllegalArgumentException("id < 1");
        }

        String getTodayOrderCount = "SELECT COUNT(*) FROM \"queries\" WHERE \"driver\"=? AND status=?;";
        int result = 0;
        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(getTodayOrderCount)){
            st.setInt(1, driverId);
            st.setString(2, QueryStatus.FINISHED.toString().toLowerCase());

            ResultSet rs = st.executeQuery();
            if(rs.next()){
                result = rs.getInt(1);
            }
        }

        logger.log(Level.FINEST, "Received a driver statistic with driver id={0} and total finished orders {1}", new Object[]{driverId, result});
        return result;
    }

    /**
     * Selects <code>UserQuery</code> object from database
     *
     * @param id of the query to be selected
     * @return a new <code>UserQuery</code> object received from database
     * @exception IllegalArgumentException if id < 1 in query
     * @exception SQLException on sql exception
     */
    public static synchronized UserQuery selectQuery(int id) throws SQLException{
        if(id < 1) throw new IllegalArgumentException("id < 1");

        UserQuery output = UserQuery.EMPTY;
        String select = "SELECT * FROM \"queries\" WHERE id=?;";
        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(select)){

            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                output = extractUserQuery(rs);
            }
        }

        return output;
    }

    /**
     * Selects <code>UserQuery</code> object from database
     *
     * @param query to be changed
     * @param activeStatus status that query will have
     * @return a result of current operation (amount of fields changed/added into database)
     * @exception IllegalArgumentException if id < 1 in query
     * @exception SQLException on sql exception
     */
    public static synchronized int updateQueryStatus(UserQuery query, QueryStatus activeStatus) throws SQLException{
        int id = query.getId();
        if(id < 1) {
            logger.log(Level.WARNING, "Passed an UserQuery object with user.id < 1");
            throw new IllegalArgumentException("id < 1");
        }

        String updateStatus = "UPDATE \"queries\" SET status=? WHERE id=?;";
        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(updateStatus)){
            st.setString(1, activeStatus.toString().toLowerCase());
            st.setInt(2, id);

            int result = st.executeUpdate();
            if(result < 1){
                logger.log(Level.WARNING, "Failed to insert a new UserQuery to DB. Customer login={0}", query.getCustomer().getLogin());
                throw new SQLException("Failed to insert a new UserQuery to DB");
            }

            return result;
        }
    }

    /**
     * Selects list of <code>UserQuery</code> objects from database that have status=active
     *
     * @return a <code>List<UserQuery></code> with QueryStatus=ACTIVE
     * @exception SQLException on sql exception
     */
    public static synchronized List<UserQuery> selectActiveQueries() throws SQLException{
        String selectActive = "SELECT * FROM \"queries\" WHERE status='active'";
        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(selectActive);
            ResultSet rs = st.executeQuery()){

            ArrayList<UserQuery> activeQueries = new ArrayList<>();
            while(rs.next()){
                activeQueries.add(extractUserQuery(rs));
            }

            logger.log(Level.FINEST, "Received a collection with active queries and size={0}", activeQueries.size());
            return activeQueries;
        }
    }

    /**
     * Service method to extract a new <code>UserQuery</code> object from ResultSet
     *
     * @param rs contains all information about current UserQuery
     * @return a new UserQuery
     * @exception SQLException on sql exception
     */
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
            if (driver.getId() > 0) driver = DriverDBController.selectDriver(driver);
        } catch (SQLException sqe) { /*do nothing*/ }

        selectedQuery.setDriver(driver);
        selectedQuery.setAddress(rs.getString("address"));
        selectedQuery.setAdditionalInformation(rs.getString("additional"));
        selectedQuery.setFeedback(rs.getString("feedback"));
        selectedQuery.setStatus(QueryStatus.valueOf(rs.getString("status").toUpperCase()));

        return selectedQuery;
    }

    /**
     * Updates driver of <code>UserQuery</code> object in database
     *
     * @param query to be changed
     * @param driver id that query will have
     * @return a result of current operation (amount of fields changed/added into database)
     * @exception IllegalArgumentException if id < 1 in query or driver < 1
     * @exception SQLException on sql exception
     */
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

        String updateDriver = "UPDATE \"queries\" SET \"driver\"=? WHERE id=?;";
        int result = 1;

        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(updateDriver)){

            st.setInt(1, driver);
            st.setInt(2, id);

            result *= st.executeUpdate();
        }

        return result * updateQueryStatus(query, QueryStatus.ACCEPTED);
    }

    /**
     * Selects <code>UserQuery</code> object with status=accepted or status=executing of specified driver from database
     *
     * @param driver id
     * @return a new UserQuery object that relates to specified driver
     * @exception IllegalArgumentException if driver < 1
     * @exception SQLException on sql exception
     */
    public static synchronized UserQuery selectActiveQuery(int driver) throws SQLException{
        if(driver < 1) throw new IllegalArgumentException("driver id < 1");

        String selectActive = "SELECT * FROM \"queries\" WHERE status IN ('accepted', 'executing') AND \"driver\"=?;";
        UserQuery activeQuery = UserQuery.EMPTY;

        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(selectActive)){

            st.setInt(1, driver);
            ResultSet rs = st.executeQuery();

            if(rs.next()){
                activeQuery = extractUserQuery(rs);
            }

            logger.log(Level.FINEST, "Received a UserQuery object from DB with driver id={0}", driver);
            return activeQuery;
        }
    }
}
