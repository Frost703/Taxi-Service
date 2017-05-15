package com.projects.taxiservice.Users.Query;

import com.projects.taxiservice.DBLogic.DBManageable;
import com.projects.taxiservice.Users.Driver.CarClass;

/**
 * Created by O'Neill on 7/3/2016.
 */

/*
Class UserQuery contains all provided information from customer. name - customer's name, address - customer's address(where the cab should arrive),
additionalInformation - additional information for driver provided by customer, phoneNumber - validated phone number, carClass - class of the car required by customer,
status - status of current query object(ACTIVE - in query, INACTIVE - not valid or was executed, EXECUTING - was taken by a driver and is currently executing.
 */
public class UserQuery implements DBManageable {

    private String name;
    private String address;
    private String additionalInformation;
    private String phoneNumber;
    private CarClass carClass;
    private QueryStatus status;

    public QueryStatus getStatus() {
        return status;
    }

    public void setStatus(QueryStatus status) {
        this.status = status;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
