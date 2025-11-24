package com.example.library-backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "library-management-backend");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        health.put("database", "Connected");
        return health;
    }
    
    @GetMapping("/detailed")
    public Map<String, Object> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "library-management-backend");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        health.put("version", "1.0.0");
        health.put("java", System.getProperty("java.version"));
        return health;
    }
}