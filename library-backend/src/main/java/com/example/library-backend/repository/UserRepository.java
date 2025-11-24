package com.example.library-backend.repository;

import com.example.library-backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.active = true") List<User> findByActiveTrue();
    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true") Long countActiveUsers();
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.active = true") Optional<User> findByIdAndActiveTrue(@Param("id") Long id);
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = true") List<User> findByRole(@Param("role") String role);
}