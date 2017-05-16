package com.projects.taxiservice.users.drivers;

import com.projects.taxiservice.dblogic.DBManageable;

import javax.persistence.*;

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
    private String login, password, name, drivingSince;
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

    public String getDrivingSince() {
        return drivingSince;
    }

    public void setDrivingSince(String drivingSince) {
        this.drivingSince = drivingSince;
    }

    @Override
    public String toString() {
        return "drivers with id " + getId() + " and name " + getName() + " with car " + car.getCarClass();
    }
}
