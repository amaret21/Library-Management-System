package com.example.library-backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank(message = "Username is required") @Column(unique = true, nullable = false) private String username;
    @NotBlank(message = "Password is required") @Column(nullable = false) private String password;
    @Email(message = "Email should be valid") @Column(unique = true, nullable = false) private String email;
    @NotBlank(message = "Full name is required") @Column(name = "full_name", nullable = false) private String fullName;
    @Column(nullable = false) private String role;
    @Builder.Default @Column(nullable = false) private Boolean active = true;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "last_login") private LocalDateTime lastLogin;
    @Column(name = "system_generated") private Boolean systemGenerated = false;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}