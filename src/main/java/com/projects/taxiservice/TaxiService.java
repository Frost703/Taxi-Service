package com.projects.taxiservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication
public class TaxiService {

    public static void main(String[] args) throws IOException, SQLException {
        SpringApplication.run(TaxiService.class, args);
    }
}
