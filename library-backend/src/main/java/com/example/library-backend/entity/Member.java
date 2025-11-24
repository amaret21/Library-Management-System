package com.example.library-backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank(message = "First name is required") @Column(name = "first_name", nullable = false) private String firstName;
    @NotBlank(message = "Last name is required") @Column(name = "last_name", nullable = false) private String lastName;
    @Email(message = "Email should be valid") @Column(unique = true) private String email;
    private String phone;
    @Column(name = "membership_id", unique = true) private String membershipId;
    private String address;
    @Column(name = "date_of_birth") private LocalDate dateOfBirth;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Builder.Default private Boolean active = true;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
    public String getFullName() { return firstName + " " + lastName; }
}