package com.projects.taxiservice.model.taxi;


/**
 * Created by O'Neill on 6/30/2016.
 */

/*
It's a Car class that is related to the specific driver. This class has fields:
carNumber - represents the plate number on the car
carDescription - short and specific description of the car(color, model, special features)
carClass - one of three classes of cars represented by the firm. Regular, FamilySize, VipClass
 */
public class Car {
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

    public static CarClass getCarClass(String car){
        switch(car.toUpperCase()){
            case "REGULAR" : return CarClass.REGULAR;
            case "FAMILY" : return CarClass.FAMILYSIZE;
            case "VIP" : return CarClass.VIPCLASS;
            default: return CarClass.REGULAR;
        }
    }
}
