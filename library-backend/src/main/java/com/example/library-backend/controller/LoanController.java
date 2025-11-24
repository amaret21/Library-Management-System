package com.example.library-backend.controller;

import com.example.library-backend.dto.ApiResponse;
import com.example.library-backend.dto.AuthResponse;
import com.example.library-backend.entity.Loan;
import com.example.library-backend.entity.User;
import com.example.library-backend.repository.UserRepository;
import com.example.library-backend.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "http://localhost:3000")
public class LoanController {
    @Autowired private LoanService loanService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // === DEBUG LOGIN ENDPOINT ===
    @PostMapping("/auth/login")
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

    // === SIMPLE LOGIN ENDPOINT ===
    @PostMapping("/auth/simple-login")
    public ResponseEntity<?> simpleLogin(@RequestBody LoginRequest loginRequest) {
        System.out.println("Simple login attempt: " + loginRequest.getUsername());

        // Direct hardcoded check - no Spring Security
        if ("admin".equals(loginRequest.getUsername()) && "admin123".equals(loginRequest.getPassword())) {
            AuthResponse response = new AuthResponse();
            response.setToken("simple-jwt-token");
            response.setUsername("admin");
            response.setRole("ROLE_ADMIN");
            response.setFullName("System Administrator");
            response.setEmail("admin@library.com");
            response.setSuccess(true);
            response.setMessage("Simple login successful");

            return ResponseEntity.ok(response);
        }

        AuthResponse errorResponse = new AuthResponse();
        errorResponse.setSuccess(false);
        errorResponse.setMessage("Invalid credentials");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // === TEST ENDPOINTS ===
    @GetMapping("/test/status")
    public ResponseEntity<?> status() {
        Map<String, String> response = Map.of(
                "status", "Server is running",
                "timestamp", LocalDate.now().toString(),
                "endpoint", "LoanController"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test/echo")
    public ResponseEntity<?> echo(@RequestBody Map<String, String> request) {
        System.out.println("Echo received: " + request);
        Map<String, Object> response = Map.of(
                "received", request,
                "server_time", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }

    // === ORIGINAL LOAN ENDPOINTS ===
    @GetMapping
    public ResponseEntity<?> getAllLoans(@RequestParam(required = false) Boolean active, @RequestParam(required = false) Boolean overdue) {
        try {
            List<Loan> loans;
            if (active != null && active) loans = loanService.findActiveLoans();
            else if (overdue != null && overdue) loans = loanService.findOverdueLoans();
            else loans = loanService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Loans retrieved successfully", loans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve loans: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanById(@PathVariable Long id) {
        try {
            Loan loan = loanService.findById(id).orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + id));
            return ResponseEntity.ok(ApiResponse.success("Loan retrieved successfully", loan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve loan: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createLoan(@RequestBody LoanRequest request) {
        try {
            Loan loan = loanService.createLoan(request.getBookId(), request.getMemberId(), request.getDueDate(), request.getNotes());
            return ResponseEntity.ok(ApiResponse.success("Loan created successfully", loan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create loan: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnLoan(@PathVariable Long id) {
        try {
            Loan returnedLoan = loanService.returnLoan(id);
            return ResponseEntity.ok(ApiResponse.success("Book returned successfully", returnedLoan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to return book: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<?> renewLoan(@PathVariable Long id, @RequestBody(required = false) Map<String, LocalDate> request) {
        try {
            LocalDate newDueDate = request != null ? request.get("dueDate") : null;
            Loan renewedLoan = loanService.renewLoan(id, newDueDate);
            return ResponseEntity.ok(ApiResponse.success("Loan renewed successfully", renewedLoan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to renew loan: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLoan(@PathVariable Long id) {
        try {
            loanService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Loan deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete loan: " + e.getMessage()));
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<?> getLoansByMember(@PathVariable Long memberId, @RequestParam(required = false) Boolean active) {
        try {
            List<Loan> loans;
            if (active != null && active) loans = loanService.findActiveLoansByMember(memberId);
            else loans = loanService.findByMember(memberId);
            return ResponseEntity.ok(ApiResponse.success("Member loans retrieved successfully", loans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve member loans: " + e.getMessage()));
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueLoans() {
        try {
            List<Loan> overdueLoans = loanService.findOverdueLoans();
            return ResponseEntity.ok(ApiResponse.success("Overdue loans retrieved successfully", overdueLoans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve overdue loans: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getLoanStatistics() {
        try {
            Long activeLoans = loanService.getActiveLoanCount();
            Long overdueLoans = loanService.getOverdueLoanCount();
            Double totalFines = loanService.calculateTotalFines();
            Map<String, Object> stats = Map.of(
                    "activeLoans", activeLoans,
                    "overdueLoans", overdueLoans,
                    "totalFines", totalFines
            );
            return ResponseEntity.ok(ApiResponse.success("Loan statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve loan statistics: " + e.getMessage()));
        }
    }

    // === REQUEST CLASSES ===
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoanRequest {
        private Long bookId;
        private Long memberId;
        private LocalDate dueDate;
        private String notes;

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}