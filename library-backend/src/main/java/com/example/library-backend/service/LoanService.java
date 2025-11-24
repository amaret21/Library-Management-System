package com.example.library-backend.service;

import com.example.library-backend.entity.Book;
import com.example.library-backend.entity.Loan;
import com.example.library-backend.entity.Member;
import com.example.library-backend.repository.BookRepository;
import com.example.library-backend.repository.LoanRepository;
import com.example.library-backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanService {
    @Autowired private LoanRepository loanRepository; @Autowired private BookRepository bookRepository; @Autowired private MemberRepository memberRepository;

    public List<Loan> findAll() { return loanRepository.findAll(); } public Optional<Loan> findById(Long id) { return loanRepository.findById(id); }
    public List<Loan> findActiveLoans() { return loanRepository.findByReturnedFalse(); } public List<Loan> findByMember(Long memberId) { return loanRepository.findByMemberId(memberId); }
    public List<Loan> findActiveLoansByMember(Long memberId) { return loanRepository.findActiveLoansByMember(memberId); } public List<Loan> findOverdueLoans() { return loanRepository.findOverdueLoans(LocalDate.now()); }

    public Loan createLoan(Long bookId, Long memberId, LocalDate dueDate, String notes) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
        if (!book.getActive()) throw new IllegalStateException("Book is not available for loan: " + book.getTitle());
        if (!member.getActive()) throw new IllegalStateException("Member account is not active: " + member.getFullName());
        if (book.getAvailableCopies() <= 0) throw new IllegalStateException("No copies available for loan for book: " + book.getTitle());
        List<Loan> activeLoans = loanRepository.findActiveLoansByMember(memberId); boolean alreadyLoaned = activeLoans.stream().anyMatch(loan -> loan.getBook().getId().equals(bookId));
        if (alreadyLoaned) throw new IllegalStateException("Member already has this book on loan: " + book.getTitle());
        if (dueDate == null) dueDate = LocalDate.now().plusDays(14); if (dueDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("Due date cannot be in the past");
        book.setAvailableCopies(book.getAvailableCopies() - 1); bookRepository.save(book);
        Loan loan = Loan.builder().book(book).member(member).loanDate(LocalDate.now()).dueDate(dueDate).notes(notes).returned(false).fineAmount(0.0).build();
        return loanRepository.save(loan);
    }

    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanId));
        if (loan.getReturned()) throw new IllegalStateException("Loan already returned"); loan.setReturned(true); loan.setActualReturnDate(LocalDate.now());
        if (loan.isOverdue()) loan.setFineAmount(loan.calculateFine()); Loan savedLoan = loanRepository.save(loan);
        Book book = loan.getBook(); book.setAvailableCopies(book.getAvailableCopies() + 1); bookRepository.save(book); return savedLoan;
    }

    public Loan renewLoan(Long loanId, LocalDate newDueDate) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanId));
        if (loan.getReturned()) throw new IllegalStateException("Cannot renew a returned loan"); if (newDueDate == null) newDueDate = LocalDate.now().plusDays(14);
        if (newDueDate.isBefore(loan.getDueDate())) throw new IllegalArgumentException("New due date cannot be before current due date");
        if (newDueDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("Due date cannot be in the past"); loan.setDueDate(newDueDate); return loanRepository.save(loan);
    }

    public void deleteById(Long id) { if (loanRepository.existsById(id)) loanRepository.deleteById(id); else throw new IllegalArgumentException("Loan not found with id: " + id); }
    public Long getActiveLoanCount() { return loanRepository.countActiveLoans(); } public Long getOverdueLoanCount() { return loanRepository.countOverdueLoans(LocalDate.now()); }
    public Double calculateTotalFines() { return loanRepository.calculateTotalFines(LocalDate.now()); }
}