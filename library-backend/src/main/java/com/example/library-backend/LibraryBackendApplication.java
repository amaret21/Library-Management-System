package com.example.library-backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.library-backend.repository")
public class LibraryBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryBackendApplication.class, args);
    }
}