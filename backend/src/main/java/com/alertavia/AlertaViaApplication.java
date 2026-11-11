package com.alertavia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AlertaViaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertaViaApplication.class, args);
    }
}
