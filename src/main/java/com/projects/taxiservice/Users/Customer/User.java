package com.projects.taxiservice.users.customer;

import com.projects.taxiservice.dblogic.DBManageable;
import com.projects.taxiservice.users.query.UserQuery;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by O'Neill on 5/15/2017.
 */
@Entity
@Table(name = "users")
public class User implements DBManageable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private int id;
    private String login, password, name, phone, address;
    @Transient
    private List<UserQuery> queries = new ArrayList<UserQuery>();
    @Transient
    private UserQuery activeQuery;
    @Transient
    public static final User EMPTY = new User().setId(-1);

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    public List<UserQuery> getQueries() {
        return queries;
    }

    public User setQueries(List<UserQuery> queries) {
        this.queries = queries;
        return this;
    }

    public UserQuery getActiveQuery() {
        return activeQuery;
    }

    public User setActiveQuery(UserQuery activeQuery) {
        this.activeQuery = activeQuery;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
