package com.projects.taxiservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

@SpringBootApplication
public class TaxiService {

    private static final Logger logger = Logger.getLogger(TaxiService.class.getName());
    static{
        try {
            logger.addHandler(new FileHandler("controllers.log", false));
        } catch(Exception e) { /*do nothing*/ }
    }

    public static void main(String[] args) throws IOException, SQLException, InterruptedException{
        SpringApplication.run(TaxiService.class, args);
    }
}

