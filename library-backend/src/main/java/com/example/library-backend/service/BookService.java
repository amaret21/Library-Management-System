package com.example.library-backend.service;

import com.example.library-backend.entity.Book;
import com.example.library-backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    @Autowired private BookRepository bookRepository;

    public List<Book> findAll() { return bookRepository.findByActiveTrue(); }
    public Optional<Book> findById(Long id) { return bookRepository.findByIdAndActiveTrue(id); }
    public Book save(Book book) {
        if (book.getAvailableCopies() == null) book.setAvailableCopies(book.getTotalCopies() != null ? book.getTotalCopies() : 0);
        if (book.getTotalCopies() == null) book.setTotalCopies(book.getAvailableCopies() != null ? book.getAvailableCopies() : 0);
        if (book.getActive() == null) book.setActive(true);
        if (book.getAvailableCopies() > book.getTotalCopies()) book.setAvailableCopies(book.getTotalCopies());
        return bookRepository.save(book); 
    }
    public void deleteById(Long id) { bookRepository.findById(id).ifPresent(book -> { book.setActive(false); bookRepository.save(book); }); }
    public List<Book> searchBooks(String keyword) { if (keyword == null || keyword.trim().isEmpty()) return findAll(); return bookRepository.searchBooks(keyword.trim()); }
    public List<Book> findAvailableBooks() { return bookRepository.findAvailableBooks(); }
    public List<Book> findByAuthor(String author) { if (author == null || author.trim().isEmpty()) return findAll(); return bookRepository.findByAuthorContainingIgnoreCase(author.trim()); }
    public List<Book> findByGenre(String genre) { if (genre == null || genre.trim().isEmpty()) return findAll(); return bookRepository.findByGenreContainingIgnoreCase(genre.trim()); }
    public Long getTotalBookCount() { Long count = bookRepository.countActiveBooks(); return count != null ? count : 0L; }
    public Long getAvailableBookCount() { List<Book> availableBooks = bookRepository.findAvailableBooks(); return availableBooks != null ? (long) availableBooks.size() : 0L; }
    public Book updateBookCopies(Long bookId, Integer newTotalCopies) {
        if (newTotalCopies == null || newTotalCopies < 0) throw new IllegalArgumentException("Total copies must be a non-negative number");
        Optional<Book> bookOpt = bookRepository.findById(bookId); if (bookOpt.isPresent()) {
            Book book = bookOpt.get(); Integer currentBorrowed = book.getTotalCopies() - book.getAvailableCopies();
            if (newTotalCopies >= currentBorrowed) { book.setTotalCopies(newTotalCopies); book.setAvailableCopies(newTotalCopies - currentBorrowed); return bookRepository.save(book);
            } else throw new IllegalArgumentException("New total copies (" + newTotalCopies + ") cannot be less than currently borrowed copies (" + currentBorrowed + ")");
        } throw new IllegalArgumentException("Book not found with id: " + bookId);
    }
    public boolean isBookAvailable(Long bookId) { Optional<Book> bookOpt = findById(bookId); return bookOpt.isPresent() && bookOpt.get().getAvailableCopies() != null && bookOpt.get().getAvailableCopies() > 0; }
    public Integer getAvailableCopies(Long bookId) { Optional<Book> bookOpt = findById(bookId); return bookOpt.map(Book::getAvailableCopies).orElse(0); }
    public Book updateBookAvailability(Long bookId, Integer availableCopiesChange) {
        Optional<Book> bookOpt = bookRepository.findById(bookId); if (bookOpt.isPresent()) {
            Book book = bookOpt.get(); Integer newAvailableCopies = book.getAvailableCopies() + availableCopiesChange;
            if (newAvailableCopies < 0) throw new IllegalStateException("Available copies cannot be negative");
            if (newAvailableCopies > book.getTotalCopies()) throw new IllegalStateException("Available copies cannot exceed total copies");
            book.setAvailableCopies(newAvailableCopies); return bookRepository.save(book);
        } throw new IllegalArgumentException("Book not found with id: " + bookId);
    }
}