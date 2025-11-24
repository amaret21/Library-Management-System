package com.example.library-backend.controller;

import com.example.library-backend.dto.ApiResponse;
import com.example.library-backend.entity.User;
import com.example.library-backend.repository.UserRepository;
import com.example.library-backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserService userService;

    @GetMapping @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> getAllUsers() {
        try { List<User> users = userService.findAll(); users.forEach(user -> user.setPassword(null)); return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve users: " + e.getMessage())); }
    }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try { Optional<User> userOptional = userService.findById(id); if (userOptional.isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
            User user = userOptional.get(); user.setPassword(null); return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve user: " + e.getMessage())); }
    }

    @PostMapping @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try { if (request.getUsername() == null || request.getUsername().trim().isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.error("Username is required"));
            if (request.getFullName() == null || request.getFullName().trim().isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.error("Full name is required"));
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.error("Email is required"));
            UserService.CreateUserRequest serviceRequest = new UserService.CreateUserRequest();
            serviceRequest.setUsername(request.getUsername().trim()); serviceRequest.setPassword(request.getPassword()); serviceRequest.setEmail(request.getEmail().trim());
            serviceRequest.setFullName(request.getFullName().trim()); serviceRequest.setRole(request.getRole() != null ? request.getRole() : "ROLE_USER");
            User savedUser = userService.createUser(serviceRequest); savedUser.setPassword(null); return ResponseEntity.ok(ApiResponse.success("User created successfully", savedUser));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create user: " + e.getMessage())); }
    }

    @PostMapping("/generate") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> generateUser(@Valid @RequestBody GenerateUserRequest request) {
        try { if (request.getFullName() == null || request.getFullName().trim().isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.error("Full name is required"));
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.error("Email is required"));
            String username = generateUsername(request.getFullName()); String password = generatePassword();
            String originalUsername = username; int counter = 1; while (userRepository.existsByUsername(username)) { username = originalUsername + counter; counter++; if (counter > 100) throw new IllegalStateException("Could not generate unique username"); }
            User user = User.builder().username(username).password(passwordEncoder.encode(password)).email(request.getEmail().trim()).fullName(request.getFullName().trim())
                    .role(request.getRole() != null ? request.getRole() : "ROLE_USER").active(true).systemGenerated(true).build();
            User savedUser = userRepository.save(user);
            UserGenerationResponse response = new UserGenerationResponse(savedUser.getId(), savedUser.getUsername(), password, savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole());
            return ResponseEntity.ok(ApiResponse.success("User generated successfully", response));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to generate user: " + e.getMessage())); }
    }

    @PutMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        try { UserService.CreateUserRequest serviceRequest = new UserService.CreateUserRequest();
            serviceRequest.setUsername(request.getUsername()); serviceRequest.setPassword(request.getPassword()); serviceRequest.setEmail(request.getEmail());
            serviceRequest.setFullName(request.getFullName()); serviceRequest.setRole(request.getRole());
            User updatedUser = userService.updateUser(id, serviceRequest); updatedUser.setPassword(null); return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update user: " + e.getMessage())); }
    }

    @PutMapping("/{id}/password") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        try { if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.error("New password is required"));
            Optional<User> userOptional = userRepository.findById(id); if (userOptional.isEmpty()) return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
            User user = userOptional.get(); user.setPassword(passwordEncoder.encode(request.getNewPassword().trim())); userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to reset password: " + e.getMessage())); }
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try { userService.deleteById(id); return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete user: " + e.getMessage())); }
    }

    @PatchMapping("/{id}/deactivate") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try { User deactivatedUser = userService.deactivateUser(id); deactivatedUser.setPassword(null); return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", deactivatedUser));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to deactivate user: " + e.getMessage())); }
    }

    @PatchMapping("/{id}/activate") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try { User activatedUser = userService.activateUser(id); activatedUser.setPassword(null); return ResponseEntity.ok(ApiResponse.success("User activated successfully", activatedUser));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to activate user: " + e.getMessage())); }
    }

    private String generateUsername(String fullName) { if (fullName == null || fullName.trim().isEmpty()) throw new IllegalArgumentException("Full name cannot be null or empty");
        String[] names = fullName.trim().split("\\s+"); StringBuilder username = new StringBuilder();
        if (names.length == 1) username.append(names[0].toLowerCase());
        else if (names.length == 2) { username.append(names[0].toLowerCase()); if (names[1].length() > 0) username.append(names[1].charAt(0)); }
        else { username.append(names[0].toLowerCase()); if (names[1].length() > 0) username.append(names[1].charAt(0)); if (names[names.length - 1].length() > 0) username.append(names[names.length - 1].charAt(0)); }
        return username.toString().toLowerCase(); }
    private String generatePassword() { return "Lib@" + UUID.randomUUID().toString().substring(0, 8); }

    public static class CreateUserRequest { private String username; private String password; private String email; private String fullName; private String role;
        public String getUsername() { return username; } public void setUsername(String username) { this.username = username; } public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; } public void setEmail(String email) { this.email = email; } public String getFullName() { return fullName; } public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; } public void setRole(String role) { this.role = role; } }
    public static class GenerateUserRequest { private String email; private String fullName; private String role;
        public String getEmail() { return email; } public void setEmail(String email) { this.email = email; } public String getFullName() { return fullName; } public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; } public void setRole(String role) { this.role = role; } }
    public static class ResetPasswordRequest { private String newPassword; public String getNewPassword() { return newPassword; } public void setNewPassword(String newPassword) { this.newPassword = newPassword; } }
    public static class UserGenerationResponse { private Long id; private String username; private String password; private String email; private String fullName; private String role;
        public UserGenerationResponse(Long id, String username, String password, String email, String fullName, String role) { this.id = id; this.username = username; this.password = password; this.email = email; this.fullName = fullName; this.role = role; }
        public Long getId() { return id; } public String getUsername() { return username; } public String getPassword() { return password; } public String getEmail() { return email; } public String getFullName() { return fullName; } public String getRole() { return role; }
    }
}