package com.chatnchu.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
    @Bean
    public CommandLineRunner debugDatasource(Environment env) {
        return args -> {
            System.out.println("=== DATASOURCE DEBUG ===");
            System.out.println("URL  = " + env.getProperty("spring.datasource.url"));
            System.out.println("USER = " + env.getProperty("spring.datasource.username"));
            System.out.println("PASS = " + env.getProperty("spring.datasource.password"));
            System.out.println("========================");
        };
    }
}

