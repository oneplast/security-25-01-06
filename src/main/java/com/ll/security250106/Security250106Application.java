package com.ll.security250106;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Security250106Application {

    public static void main(String[] args) {
        SpringApplication.run(Security250106Application.class, args);
    }

}
