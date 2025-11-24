package com.example.library-backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Backend is working! Package: com.example.library_backend";
    }

    @GetMapping("/api/test")
    public String apiTest() {
        return "API endpoint is working!";
    }
}