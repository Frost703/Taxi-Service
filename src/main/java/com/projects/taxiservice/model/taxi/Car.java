package com.projects.taxiservice.model.taxi;


import com.projects.taxiservice.persistent.DBManageable;

/**
 * This class is a class-helper that represents all information about the car of a <code>Driver</code>
 * Used only with <code>Driver</code> object
 */

public class Car implements DBManageable {
    public static final Car EMPTY = new Car();
    private int id;
    private String carNumber;
    private String carDescription;
    private CarClass carClass;

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarDescription() {
        return carDescription;
    }

    public void setCarDescription(String carDescription) {
        this.carDescription = carDescription;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
