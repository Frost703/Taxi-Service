package com.projects.taxiservice.dblogic.classcontrollers;

import com.projects.taxiservice.dblogic.DBController;
import com.projects.taxiservice.users.drivers.Car;
import com.projects.taxiservice.users.drivers.CarClass;
import com.projects.taxiservice.users.drivers.Driver;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by O'Neill on 5/16/2017.
 */
public class DriverDBController {
    private final Connection con;
    private static final Logger logger = Logger.getLogger(DriverDBController.class.getName());

    public DriverDBController(Connection con) {
        if(con == null) throw new IllegalArgumentException("Connection object cannot be null!");
        this.con = con;
        logger.addHandler(DBController.getLogHandler());
    }

    public Object execute(String operation, Driver driver) throws SQLException {
        Object output = null;
        switch(operation.toLowerCase()){
            case "get" : output = selectDriver(driver);
                break;
            case "register" : output = insertDriver(driver);
                break;
            default: output = null;
                throw new IllegalArgumentException("Operation not recognized! Operation: " + operation);
        }

        return output;
    }

    private Driver selectDriver(Driver driver) throws SQLException{
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
                        .setDrivingSince(rs.getTimestamp("drivingsince").toLocalDateTime().toLocalDate());
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
                    switch(carType) {
                        case "regular":
                            car.setCarClass(CarClass.REGULAR);
                            break;
                        case "family" : car.setCarClass(CarClass.FAMILYSIZE);
                            break;
                        case "vip" : car.setCarClass(CarClass.VIPCLASS);
                            break;
                        default : car.setCarClass(CarClass.REGULAR);
                    }
                }
                driverStored.setCar(car);
            }
            rs.close();
            st.close();

            logger.log(Level.FINEST, "Returned an object from DB with id={0}, login={1} and car id={2}"
                    , new Object[] {driverStored.getId(), driverStored.getLogin(), driverStored.getCar().getId()});
            return driverStored;
        } catch(Exception e) {
            throw e;
        }
    }

    private Driver insertDriver(Driver driver) throws SQLException {
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

            st.execute();

            //select the last car entity to get it's id
            st = con.prepareStatement(selectLastCar);
            ResultSet rs = st.executeQuery();
            int carId = 0;
            while(rs.next()){
                carId = rs.getInt("id");
            }

            if(carId < 1) throw new SQLException("Failed to insert a new car record to DB. Plate: " + car.getCarNumber());

            //insert a new driver to DB
            st = con.prepareStatement(insertDriver);
            st.setString(1, driver.getLogin());
            st.setString(2, driver.getPassword());
            st.setString(3, driver.getName());
            st.setTimestamp(4, Timestamp.valueOf(driver.getDrivingSince().atStartOfDay()));
            st.setInt(5, carId);

            st.execute();

            st.close();
            rs.close();

            //check for successful input
            Driver driverStored = selectDriver(driver.setId(-1));
            if(driverStored.getId() < 1) throw new SQLException("Failed to insert a new driver to DB. Login: " + driver.getLogin());

            return driverStored;
        } catch (Exception e) {
            throw e;
        }
    }
}
