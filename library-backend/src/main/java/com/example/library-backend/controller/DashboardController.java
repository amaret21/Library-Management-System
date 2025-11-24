package com.example.library-backend.controller;

import com.example.library-backend.dto.ApiResponse;
import com.example.library-backend.service.BookService;
import com.example.library-backend.service.LoanService;
import com.example.library-backend.service.MemberService;
import com.example.library-backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired private BookService bookService;
    @Autowired private MemberService memberService;
    @Autowired private UserService userService;
    @Autowired private LoanService loanService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            Long totalBooks = bookService.getTotalBookCount(); Long availableBooks = bookService.getAvailableBookCount();
            stats.put("totalBooks", totalBooks != null ? totalBooks : 0L); stats.put("availableBooks", availableBooks != null ? availableBooks : 0L);
            stats.put("borrowedBooks", (totalBooks != null && availableBooks != null) ? totalBooks - availableBooks : 0L);
            Long totalMembers = memberService.getTotalMemberCount(); stats.put("totalMembers", totalMembers != null ? totalMembers : 0L);
            Long totalUsers = userService.getTotalUserCount(); stats.put("totalUsers", totalUsers != null ? totalUsers : 0L);
            Long activeLoans = loanService.getActiveLoanCount(); Long overdueLoans = loanService.getOverdueLoanCount(); Double totalFines = loanService.calculateTotalFines();
            stats.put("activeLoans", activeLoans != null ? activeLoans : 0L); stats.put("overdueLoans", overdueLoans != null ? overdueLoans : 0L); stats.put("totalFines", totalFines != null ? totalFines : 0.0);
            return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", stats));
        } catch (Exception e) { e.printStackTrace(); return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve dashboard statistics: " + e.getMessage())); }
    }
}