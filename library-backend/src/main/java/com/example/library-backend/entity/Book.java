package com.example.library-backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank(message = "Title is required") @Column(nullable = false) private String title;
    @NotBlank(message = "Author is required") @Column(nullable = false) private String author;
    @Column(unique = true) private String isbn;
    private String publisher;
    @Column(name = "publication_year") private Integer publicationYear;
    private String genre;
    @Column(length = 1000) private String description;
    @Min(value = 0, message = "Total copies cannot be negative") @Column(name = "total_copies", nullable = false) private Integer totalCopies;
    @Min(value = 0, message = "Available copies cannot be negative") @Column(name = "available_copies", nullable = false) private Integer availableCopies;
    @Builder.Default private Boolean active = true;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() {
        createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now();
        if (availableCopies == null) availableCopies = totalCopies;
    }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
	public Double getReplacementCost() {
		// TODO Auto-generated method stub
		return null;
	}
}