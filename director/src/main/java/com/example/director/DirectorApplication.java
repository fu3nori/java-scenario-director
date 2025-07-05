package com.example.director;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.director.model")
public class DirectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DirectorApplication.class, args);
    }

}
