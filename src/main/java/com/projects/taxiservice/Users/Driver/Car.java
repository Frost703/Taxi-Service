package com.projects.taxiservice.Users.Driver;

import javax.persistence.Embeddable;

/**
 * Created by O'Neill on 6/30/2016.
 */

/*
It's a Car class that is related to the specific driver. This class has fields:
carNumber - represents the plate number on the car
carDescription - short and specific description of the car(color, model, special features)
carClass - one of three classes of cars represented by the firm. Regular, FamilySize, VipClass
 */
@Embeddable //@Embedded - for marking the variable. Works the same as @Embeddable
public class Car {
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


}
