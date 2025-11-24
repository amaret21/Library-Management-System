package com.example.library-backend.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String fullName;
    private String email;
    private boolean success;
    private String message;

    // Default constructor
    public AuthResponse() {}

    // Constructor for successful login
    public AuthResponse(String token, String username, String role, String fullName, String email) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.success = true;
        this.message = "Login successful";
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}