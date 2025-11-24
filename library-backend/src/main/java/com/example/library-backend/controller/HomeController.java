package com.example.library-backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Library Management System Backend is running! " +
               "Use the frontend at http://localhost:3000 to access the application. " +
               "API endpoints are available at /api/";
    }
}