package com.projects.taxiservice;


import com.projects.taxiservice.users.drivers.CarClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication
public class TaxiService {

    public static void main(String[] args) throws IOException, SQLException, InterruptedException{
        SpringApplication.run(TaxiService.class, args);
    }
}

