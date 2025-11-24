package com.example.library-backend.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String fullName;
    private String role;
    private String email;
    private String message;
    private Long userId;

    public JwtResponse(String token, String username, String fullName, String role, String email, String message, Long userId) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.message = message;
        this.userId = userId;
    }
}