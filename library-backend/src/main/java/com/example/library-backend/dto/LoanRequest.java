package com.example.library-backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanRequest {
    @NotNull(message = "Book ID is required")
    private Long bookId;
    @NotNull(message = "Member ID is required")
    private Long memberId;
    private LocalDate dueDate;
    private String notes;
}