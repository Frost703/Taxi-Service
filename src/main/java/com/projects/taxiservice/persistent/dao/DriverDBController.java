package com.projects.taxiservice.persistent.dao;

import com.projects.taxiservice.model.taxi.CarClass;
import com.projects.taxiservice.persistent.DBController;
import com.projects.taxiservice.model.taxi.Car;
import com.projects.taxiservice.model.taxi.Driver;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Performs database operations with <code>Driver</code> objects.
 */
public final class DriverDBController {
    private static final Logger logger = Logger.getLogger(DriverDBController.class.getName());

    private DriverDBController() {}

    /**
     * Selects <code>Driver</code> object from database
     *
     * @param driver must have id or login
     * @return a new <code>Driver</code> object with all information that is stored in database
     * @exception IllegalArgumentException if id or login is not specified in driver
     * @exception SQLException on sql exception
     */
    public static synchronized Driver selectDriver(Driver driver) throws SQLException{
        if(driver == null) {
            logger.log(Level.WARNING, "Passed a null driver to selectDriver() method");
            throw new IllegalArgumentException("Can't perform select driver statement. Passed Driver object is null");
        }
        if(driver.getId() < 1 && (driver.getLogin() == null || driver.getLogin().length() < 3)) {
            logger.log(Level.WARNING, "Passed a driver object with (id<1) and empty login) to selectDriver() method");
            throw new IllegalArgumentException("Can't perform select driver statement. Id or login must be provided");
        }
        String selectDriver = "SELECT * FROM \"drivers\" WHERE ";
        Driver driverStored = new Driver();

        boolean isIdAvailable;
        if (driver.getId() > 0) {
            selectDriver += "id=?;";
            isIdAvailable = true;
        } else {
            selectDriver += "login=?;";
            isIdAvailable = false;
        }

        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(selectDriver)) {
            ResultSet rs;
            if(isIdAvailable){
                st.setInt(1, driver.getId());
            }
            else{
                st.setString(1, driver.getLogin());
            }

            int carId = 0;
            rs = st.executeQuery();
            if (rs.next()) {
                driverStored.setId(rs.getInt("id")).setLogin(rs.getString("login")).setName(rs.getString("name"))
                            .setPassword(rs.getString("password"))
                            .setDrivingSince(rs.getDate("drivingsince").toLocalDate());
                carId = rs.getInt("carid");
            }

            if (carId < 1) driverStored.setCar(Car.EMPTY);
            else {
                String selectCar = "SELECT * FROM \"cars\" WHERE id=?;";
                Car car = new Car();
                try(PreparedStatement stat = con.prepareStatement(selectCar)) {
                    stat.setInt(1, carId);

                    rs = stat.executeQuery();
                    while (rs.next()) {
                        car.setCarDescription(rs.getString("description"));
                        car.setCarNumber(rs.getString("plate"));

                        String carType = rs.getString("class");
                        car.setCarClass(CarClass.valueOf(carType));
                    }
                    driverStored.setCar(car);
                }
            }
        }

        logger.log(Level.FINEST, "Returned a driver from DB with id={0}", driverStored.getId());
        return driverStored;
    }

    /**
     * Inserts <code>Driver</code> object into database
     * Inserts <code>Car</code> object into database that is related to current <code>Driver</code>
     *
     * @param driver driver object to insert. Must have a car
     * @return a <code>Driver</code> object with id from database
     * @exception IllegalArgumentException if id or login or name or Car is not specified in driver
     * @exception SQLException on sql exception
     */
    public static synchronized Driver insertDriver(Driver driver) throws SQLException {
        String insertDriver = "INSERT INTO \"drivers\" " +
                "(login, password, name, drivingsince, carid) VALUES " +
                "(?,?,?,?,?);";

        String insertCar = "INSERT INTO \"cars\" " +
                "(plate, description, class) VALUES " +
                "(?,?,?);";

        if(driver == null) {
            logger.log(Level.WARNING, "Passed a null driver to insertDriver() method");
            throw new IllegalArgumentException("Can't perform select driver statement. Passed Driver object is null");
        }
        if(driver.getLogin() == null || driver.getLogin().length() < 3) {
            logger.log(Level.WARNING, "Passed a driver object with empty login to insertDriver() method");
            throw new IllegalArgumentException("Can't perform select driver statement. Id or login must be provided");
        }

        if(driver.getName() == null || driver.getName().length() < 3) {
            logger.log(Level.WARNING, "Passed a driver object with empty name to insertDriver() method");
            throw new IllegalArgumentException("Driver name cannot be empty");
        }

        if(driver.getCar() == null || driver.getCar().getCarNumber().length() < 3) {
            logger.log(Level.WARNING, "Passed a driver object with empty car to insertDriver() method");
            throw new IllegalArgumentException("Driver must have a car object fully initialized");
        }

        //insert a new car to DB
        try(Connection con = DBController.getConnection();
            PreparedStatement st = con.prepareStatement(insertCar, Statement.RETURN_GENERATED_KEYS)) {

            Car car = driver.getCar();
            st.setString(1, car.getCarNumber());
            st.setString(2, car.getCarDescription());
            st.setString(3, car.getCarClass().name().toLowerCase());

            int carId = 0;
            int carsInserted = st.executeUpdate();

            if(carsInserted < 1) {
                logger.log(Level.WARNING, "Failed to insert a new car to db. CarNumber=[0]", car.getCarNumber());
                throw new SQLException("Failed to insert a new Car to DB");
            }
            else{
                ResultSet rs = st.getGeneratedKeys();
                if(rs.next()){
                    carId = rs.getInt(1);
                    if(carId < 1){
                        logger.log(Level.WARNING, "Failed to insert a new car to db. CarNumber=[0]", car.getCarNumber());
                        throw new SQLException("Failed to insert a new Car to DB");
                    }
                }
            }

            try(PreparedStatement stat = con.prepareStatement(insertDriver, Statement.RETURN_GENERATED_KEYS)) {
                //insert a new driver to DB
                stat.setString(1, driver.getLogin());
                stat.setString(2, driver.getPassword());
                stat.setString(3, driver.getName());
                stat.setDate(4, Date.valueOf(driver.getDrivingSince()));
                stat.setInt(5, carId);

                int driversInserted = stat.executeUpdate();
                if(driversInserted < 1){
                    logger.log(Level.WARNING, "Failed to insert a new driver to db with login", driver.getLogin());
                    throw new SQLException("Failed to insert a new Driver to DB");
                }
                else{
                    ResultSet rs = stat.getGeneratedKeys();
                    if(rs.next()){
                        int driverId = rs.getInt(1);
                        if(driverId < 1){
                            logger.log(Level.WARNING, "Failed to insert a new driver to db with login", driver.getLogin());
                            throw new SQLException("Failed to insert a new Driver to DB");
                        }

                        driver.setId(driverId);
                    }
                }
            }
        }

        return driver;
    }
}
