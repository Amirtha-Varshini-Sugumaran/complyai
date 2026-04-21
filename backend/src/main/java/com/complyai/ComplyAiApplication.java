package com.complyai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ComplyAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComplyAiApplication.class, args);
    }
}
