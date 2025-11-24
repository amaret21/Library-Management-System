package com.example.library-backend.service;

import com.example.library-backend.entity.User;
import com.example.library-backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    @Autowired private UserRepository userRepository; @Autowired private PasswordEncoder passwordEncoder;

    public List<User> findAll() { return userRepository.findByActiveTrue(); } public Optional<User> findById(Long id) { return userRepository.findByIdAndActiveTrue(id); }
    public User save(User user) { return userRepository.save(user); }
    public void deleteById(Long id) { userRepository.findById(id).ifPresent(user -> { user.setActive(false); userRepository.save(user); }); }

    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        String password = request.getPassword(); if (password == null || password.trim().isEmpty()) password = generateRandomPassword();
        User user = User.builder().username(request.getUsername()).password(passwordEncoder.encode(password)).fullName(request.getFullName()).email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() : "ROLE_USER").active(true).systemGenerated(false).build();
        return userRepository.save(user);
    }

    public User updateUser(Long id, CreateUserRequest request) {
        Optional<User> userOpt = userRepository.findById(id); if (userOpt.isPresent()) {
            User user = userOpt.get(); if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) throw new IllegalArgumentException("Username already exists: " + request.getUsername());
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            user.setUsername(request.getUsername()); user.setFullName(request.getFullName()); user.setEmail(request.getEmail());
            if (request.getRole() != null) user.setRole(request.getRole()); if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) user.setPassword(passwordEncoder.encode(request.getPassword()));
            return userRepository.save(user);
        } throw new IllegalArgumentException("User not found with id: " + id);
    }

    public User changePassword(Long userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId); if (userOpt.isPresent()) {
            User user = userOpt.get(); user.setPassword(passwordEncoder.encode(newPassword)); return userRepository.save(user);
        } throw new IllegalArgumentException("User not found with id: " + userId);
    }

    public List<User> findByRole(String role) { return userRepository.findByRole(role); } public Long getTotalUserCount() { Long count = userRepository.countActiveUsers(); return count != null ? count : 0L; }
    private String generateRandomPassword() { return "Lib@" + UUID.randomUUID().toString().substring(0, 8); }
    public User deactivateUser(Long userId) { Optional<User> userOpt = userRepository.findById(userId); if (userOpt.isPresent()) { User user = userOpt.get(); user.setActive(false); return userRepository.save(user); } throw new IllegalArgumentException("User not found with id: " + userId); }
    public User activateUser(Long userId) { Optional<User> userOpt = userRepository.findById(userId); if (userOpt.isPresent()) { User user = userOpt.get(); user.setActive(true); return userRepository.save(user); } throw new IllegalArgumentException("User not found with id: " + userId); }

    public static class CreateUserRequest { private String username; private String password; private String email; private String fullName; private String role;
        public String getUsername() { return username; } public void setUsername(String username) { this.username = username; } public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; } public void setEmail(String email) { this.email = email; } public String getFullName() { return fullName; } public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; } public void setRole(String role) { this.role = role; } }
}