package com.example.library-backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Loan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "book_id", nullable = false) @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) private Book book;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "member_id", nullable = false) @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) private Member member;
    @Column(name = "loan_date") private LocalDate loanDate;
    @Column(name = "due_date", nullable = false) private LocalDate dueDate;
    @Column(name = "return_date") private LocalDate returnDate;
    @Column(name = "actual_return_date") private LocalDate actualReturnDate;
    @Column(nullable = false) @Builder.Default private Boolean returned = false;
    @Column(name = "fine_amount") private Double fineAmount;
    @Column(length = 500) private String notes;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;

    @PrePersist protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (loanDate == null) loanDate = LocalDate.now();
        if (fineAmount == null) fineAmount = 0.0;
    }
    @JsonProperty("isOverdue")
    public boolean isOverdue() {
        if (dueDate == null || returned) return false;
        return !returned && LocalDate.now().isAfter(dueDate); }
    @JsonProperty("daysOverdue")
    public long getDaysOverdue() {
        if (!isOverdue() || dueDate == null) return 0;
        return LocalDate.now().toEpochDay() - dueDate.toEpochDay();
    }
    @JsonProperty("calculatedFine")
    public Double calculateFine() { if (!isOverdue()) return 0.0;
        long overdueDays = getDaysOverdue();
        return overdueDays * 0.50; }
}