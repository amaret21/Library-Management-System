package com.example.library-backend.repository;

import com.example.library-backend.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberId(Long memberId);
    List<Loan> findByBookId(Long bookId);
    @Query("SELECT l FROM Loan l WHERE l.returned = false") List<Loan> findByReturnedFalse();
    @Query("SELECT l FROM Loan l WHERE l.returned = true") List<Loan> findByReturnedTrue();
    @Query("SELECT l FROM Loan l WHERE l.returned = false AND l.dueDate < :currentDate") List<Loan> findOverdueLoans(@Param("currentDate") LocalDate currentDate);
    @Query("SELECT l FROM Loan l WHERE l.member.id = :memberId AND l.returned = false") List<Loan> findActiveLoansByMember(@Param("memberId") Long memberId);
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.returned = false") Long countActiveLoans();
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.returned = false AND l.dueDate < :currentDate") Long countOverdueLoans(@Param("currentDate") LocalDate currentDate);
    @Query("SELECT COALESCE(SUM(l.fineAmount), 0) FROM Loan l WHERE l.returned = false AND l.dueDate < :currentDate") Double calculateTotalFines(@Param("currentDate") LocalDate currentDate);
}