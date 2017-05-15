package com.projects.taxiservice.Users.Driver;

import com.projects.taxiservice.DBLogic.DBManageable;

import javax.persistence.*;

/**
 * Created by O'Neill on 6/30/2016.
 */

/*
Class Driver that represents a car taxi driver in a database. This driver has such fields:
id - driver's number in the DB
login - his login to personal account
password - his password to personal account.*(To be encrypted and decrypted)
name - driver's name that is used in his account and in customer's GUI
car - the car that driver uses that contains it's description
 */


    /*  @Lob - large object - for objects that are bigger than 255chars
        @Column (name = "") - to change the column names
        @Transient - to not save this field to DB
        @Temporal (TemporalType.Date) - to change the date storing format. f.e. TemporalType.Time - saves only time
     */

@Entity
@Table(name = "Drivers")
public class Driver implements DBManageable {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO )
    public int id;
    private String login, password, name;
    private Car car;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "Driver with id " + getId() + " and name " + getName() + " with car " + car.getCarClass();
    }
}
