package com.projects.taxiservice.dblogic.dao;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.drivers.Car;
import com.projects.taxiservice.users.drivers.CarClass;
import com.projects.taxiservice.users.drivers.Driver;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by O'Neill on 5/16/2017.
 */
public final class DriverDBController {
    private static Connection con;
    private static final Logger logger = Logger.getLogger(DBController.class.getName());

    private DriverDBController() {}

    static{
        setConnection(DBController.getConnection());
    }

    public static void setConnection(Connection connection) {
        if(connection == null) throw new IllegalArgumentException("Connection object cannot be null!");
        con = connection;
    }

    public static Driver selectDriver(Driver driver) throws SQLException{
        if(driver == null) throw new IllegalArgumentException("Can't perform select driver statement. Passed Driver object is null");
        if(driver.getId() < 1 && (driver.getLogin() == null || driver.getLogin().length() < 3))
            throw new IllegalArgumentException("Can't perform select driver statement. Id or login must be provided");

        String selectDriver = "SELECT * FROM \"drivers\" WHERE ";
        ResultSet rs = null;
        PreparedStatement st = null;
        try {
            if (driver.getId() > 0) {
                selectDriver += "id=?;";
                st = con.prepareStatement(selectDriver);
                st.setInt(1, driver.getId());
            } else {
                selectDriver += "login=?;";
                st = con.prepareStatement(selectDriver);
                st.setString(1, driver.getLogin());
            }

            Driver driverStored = new Driver();
            int carId = 0;
            rs = st.executeQuery();
            while(rs.next()){
                driverStored.setId(rs.getInt("id")).setLogin(rs.getString("login")).setName(rs.getString("name"))
                        .setPassword(rs.getString("password"))
                        .setDrivingSince(rs.getDate("drivingsince").toLocalDate());
                carId = rs.getInt("carid");
            }

            if(carId < 1) driverStored.setCar(null);
            else {
                String selectCar = "SELECT * FROM \"cars\" WHERE id=?;";
                Car car = new Car();
                st = con.prepareStatement(selectCar);
                st.setInt(1, carId);

                rs = st.executeQuery();
                while (rs.next()) {
                    car.setCarDescription(rs.getString("description"));
                    car.setCarNumber(rs.getString("plate"));

                    String carType = rs.getString("class");
                    car.setCarClass(Car.getCarClass(carType));
                }
                driverStored.setCar(car);
            }
            rs.close();
            st.close();

            logger.log(Level.FINEST, "Returned an object from DB with id={0}",driverStored.getId());
            return driverStored;
        } catch(Exception e) {
            throw e;
        }
    }

    public static Driver insertDriver(Driver driver) throws SQLException {
        String insertDriver = "INSERT INTO \"drivers\" " +
                "(login, password, name, drivingsince, carid) VALUES " +
                "(?,?,?,?,?);";

        String insertCar = "INSERT INTO \"cars\" " +
                "(plate, description, class) VALUES " +
                "(?,?,?);";
        String selectLastCar = "SELECT * FROM \"cars\" ORDER BY id DESC LIMIT 1;";

        if(driver == null) throw new IllegalArgumentException("Can't perform select driver statement. Passed Driver object is null");
        if(driver.getId() < 1 && (driver.getLogin() == null || driver.getLogin().length() < 3))
            throw new IllegalArgumentException("Can't perform select driver statement. Id or login must be provided");

        if(driver.getName() == null || driver.getName().length() < 3)
            throw new IllegalArgumentException("Driver name cannot be empty");

        if(driver.getCar() == null || driver.getCar().getCarNumber().length() < 3)
            throw new IllegalArgumentException("Driver must have a car object fully initialized");

        try{
            //insert a new car to DB
            PreparedStatement st = con.prepareStatement(insertCar);
            Car car = driver.getCar();
            st.setString(1, car.getCarNumber());
            st.setString(2, car.getCarDescription());
            st.setString(3, car.getCarClass().name().toLowerCase());

            st.executeUpdate();

            //select the last car entity to get it's id
            st = con.prepareStatement(selectLastCar);
            ResultSet rs = st.executeQuery();
            int carId = 0;
            while(rs.next()){
                carId = rs.getInt("id");
            }
            rs.close();

            if(carId < 1) throw new SQLException("Failed to insert a new car record to DB. Plate: " + car.getCarNumber());

            //insert a new driver to DB
            st = con.prepareStatement(insertDriver);
            st.setString(1, driver.getLogin());
            st.setString(2, driver.getPassword());
            st.setString(3, driver.getName());
            st.setDate(4, Date.valueOf(driver.getDrivingSince()));
            st.setInt(5, carId);

            st.executeUpdate();

            st.close();

            //check for successful input and get driver's id
            Driver driverStored = selectDriver(driver.setId(-1));
            if(driverStored.getId() < 1) throw new SQLException("Failed to insert a new driver to DB. Login: " + driver.getLogin());

            return driverStored;
        } catch (Exception e) {
            throw e;
        }
    }
}
