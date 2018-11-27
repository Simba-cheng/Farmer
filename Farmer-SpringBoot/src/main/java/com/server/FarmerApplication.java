package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author CYX
 */
@SpringBootApplication
public class FarmerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(FarmerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FarmerApplication.class, args);
    }
}
