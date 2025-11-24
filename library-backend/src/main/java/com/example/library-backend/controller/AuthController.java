package com.example.library-backend.controller;

import com.example.library-backend.dto.ApiResponse;
import com.example.library-backend.dto.AuthResponse;
import com.example.library-backend.entity.User;
import com.example.library-backend.repository.UserRepository;
import com.example.library-backend.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("=== LOGIN DEBUG START ===");
            System.out.println("Username: " + loginRequest.getUsername());
            System.out.println("Password provided: " + loginRequest.getPassword());
            System.out.println("Request received at: " + java.time.LocalDateTime.now());
            
            // Test with hardcoded credentials first - bypass Spring Security
            if ("admin".equals(loginRequest.getUsername()) && "admin123".equals(loginRequest.getPassword())) {
                System.out.println("✅ Hardcoded credentials matched!");
                
                // Create successful response
                AuthResponse response = new AuthResponse();
                response.setToken("jwt-test-token-admin-" + System.currentTimeMillis());
                response.setUsername("admin");
                response.setRole("ROLE_ADMIN");
                response.setFullName("System Administrator");
                response.setEmail("admin@library.com");
                response.setSuccess(true);
                response.setMessage("Login successful - DEBUG MODE");
                
                System.out.println("Returning success response for admin");
                System.out.println("=== LOGIN DEBUG SUCCESS ===");
                return ResponseEntity.ok(response);
            }
            
            if ("librarian".equals(loginRequest.getUsername()) && "lib123".equals(loginRequest.getPassword())) {
                System.out.println("✅ Hardcoded credentials matched!");
                
                // Create successful response
                AuthResponse response = new AuthResponse();
                response.setToken("jwt-test-token-librarian-" + System.currentTimeMillis());
                response.setUsername("librarian");
                response.setRole("ROLE_LIBRARIAN");
                response.setFullName("Library Manager");
                response.setEmail("librarian@library.com");
                response.setSuccess(true);
                response.setMessage("Login successful - DEBUG MODE");
                
                System.out.println("Returning success response for librarian");
                System.out.println("=== LOGIN DEBUG SUCCESS ===");
                return ResponseEntity.ok(response);
            }
            
            // Test other possible credentials
            if ("admin".equals(loginRequest.getUsername())) {
                System.out.println("❌ Admin username correct but password wrong");
                System.out.println("Expected: admin123");
                System.out.println("Received: " + loginRequest.getPassword());
            }
            
            if ("librarian".equals(loginRequest.getUsername())) {
                System.out.println("❌ Librarian username correct but password wrong");
                System.out.println("Expected: lib123");
                System.out.println("Received: " + loginRequest.getPassword());
            }
            
            System.out.println("❌ No hardcoded credentials matched");
            System.out.println("Available test users:");
            System.out.println("  - admin / admin123");
            System.out.println("  - librarian / lib123");
            System.out.println("=== LOGIN DEBUG FAILED ===");
            
            // If credentials don't match
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Invalid username or password. Try: admin/admin123 or librarian/lib123");
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            System.err.println("=== LOGIN DEBUG ERROR ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Stack trace:");
            e.printStackTrace();
            System.err.println("=== LOGIN DEBUG ERROR END ===");
            
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/logout") 
    public ResponseEntity<?> logout() { 
        SecurityContextHolder.clearContext(); 
        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage("Logout successful");
        return ResponseEntity.ok(response); 
    }
    
    @GetMapping("/me") 
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Not authenticated");
            return ResponseEntity.status(401).body(errorResponse);
        }
        
        String username = authentication.getName(); 
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("User not found");
            return ResponseEntity.status(404).body(errorResponse);
        }
        
        User user = userOptional.get(); 
        AuthResponse authResponse = new AuthResponse(
            null, // No token for current user endpoint
            user.getUsername(),
            user.getRole(),
            user.getFullName(),
            user.getEmail()
        );
        authResponse.setSuccess(true);
        authResponse.setMessage("User details retrieved");
        
        return ResponseEntity.ok(authResponse);
    }

    // Simple test login without database
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody LoginRequest request) {
        System.out.println("Test login for: " + request.getUsername());
        
        // Hardcoded test users
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            String token = jwtUtil.generateToken(
                new org.springframework.security.core.userdetails.User(
                    "admin", "password", java.util.Collections.emptyList()
                )
            );
            
            AuthResponse authResponse = new AuthResponse(
                token,
                "admin",
                "ROLE_ADMIN",
                "Test Admin",
                "admin@test.com"
            );
            
            return ResponseEntity.ok(authResponse);
        }
        
        AuthResponse errorResponse = new AuthResponse();
        errorResponse.setSuccess(false);
        errorResponse.setMessage("Invalid test credentials");
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error fetching users"));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword() {
        try {
            Optional<User> adminUser = userRepository.findByUsername("admin");
            
            if (adminUser.isPresent()) {
                User admin = adminUser.get();
                
                // Reset password with proper BCrypt encoding
                String newPassword = "admin123";
                String encodedPassword = passwordEncoder.encode(newPassword);
                admin.setPassword(encodedPassword);
                admin.setActive(true);
                userRepository.save(admin);
                
                Map<String, String> response = new HashMap<>();
                response.put("message", "Admin password reset successfully");
                response.put("username", "admin");
                response.put("password", newPassword);
                response.put("encoded_password", encodedPassword);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Admin user not found");
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    public static class LoginRequest { 
        private String username; 
        private String password; 
        
        public String getUsername() { return username; } 
        public void setUsername(String username) { this.username = username; } 
        public String getPassword() { return password; } 
        public void setPassword(String password) { this.password = password; } 
    }
}