package com.projects.taxiservice.users.drivers;

import com.projects.taxiservice.dblogic.DBManageable;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by O'Neill on 6/30/2016.
 */

    /*  @Lob - large object - for objects that are bigger than 255chars
        @Column (name = "") - to change the column names
        @Transient - to not save this field to DB
        @Temporal (TemporalType.Date) - to change the date storing format. f.e. TemporalType.Time - saves only time
     */

@Entity
@Table(name = "drivers")
public class Driver implements DBManageable {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO )
    private int id;
    private LocalDate drivingSince;
    private String login, password, name;
    private Car car;

    public Car getCar() {
        return car;
    }

    public Driver setCar(Car car) {
        this.car = car;
        return this;
    }

    public int getId() {
        return id;
    }

    public Driver setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Driver setName(String name) {
        this.name = name;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public Driver setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Driver setPassword(String password) {
        this.password = password;
        return this;
    }

    public LocalDate getDrivingSince() {
        return drivingSince;
    }

    public Driver setDrivingSince(LocalDate drivingSince) {
        this.drivingSince = drivingSince;
        return this;
    }

    @Override
    public String toString() {
        return "drivers with id " + getId() + " and name " + getName() + " with car " + car.getCarClass();
    }
}
