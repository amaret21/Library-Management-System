package com.example.library-backend.controller;

import com.example.library-backend.dto.ApiResponse;
import com.example.library-backend.entity.Book;
import com.example.library-backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {
    @Autowired private BookService bookService;

    @GetMapping public ResponseEntity<?> getAllBooks(@RequestParam(required = false) String search, @RequestParam(required = false) String author, @RequestParam(required = false) String genre, @RequestParam(required = false) Boolean available) {
        try { List<Book> books; 
            if (search != null && !search.isBlank()) books = bookService.searchBooks(search);
            else if (author != null && !author.isBlank()) books = bookService.findByAuthor(author);
            else if (genre != null && !genre.isBlank()) books = bookService.findByGenre(genre);
            else if (available != null && available) books = bookService.findAvailableBooks();
            else books = bookService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", books));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve books: " + e.getMessage())); }
    }

    @GetMapping("/{id}") public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try { Book book = bookService.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id)); return ResponseEntity.ok(ApiResponse.success("Book retrieved successfully", book));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve book: " + e.getMessage())); }
    }

    @PostMapping public ResponseEntity<?> createBook(@RequestBody Book book) {
        try { Book savedBook = bookService.save(book); return ResponseEntity.ok(ApiResponse.success("Book created successfully", savedBook));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create book: " + e.getMessage())); }
    }

    @PutMapping("/{id}") public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book book) {
        try { Book existingBook = bookService.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));
            existingBook.setTitle(book.getTitle()); existingBook.setAuthor(book.getAuthor()); existingBook.setIsbn(book.getIsbn()); existingBook.setDescription(book.getDescription());
            existingBook.setPublisher(book.getPublisher()); existingBook.setPublicationYear(book.getPublicationYear()); existingBook.setGenre(book.getGenre());
            existingBook.setTotalCopies(book.getTotalCopies()); existingBook.setAvailableCopies(book.getAvailableCopies());
            Book updatedBook = bookService.save(existingBook); return ResponseEntity.ok(ApiResponse.success("Book updated successfully", updatedBook));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update book: " + e.getMessage())); }
    }

    @DeleteMapping("/{id}") public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try { bookService.deleteById(id); return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete book: " + e.getMessage())); }
    }

    @GetMapping("/available") public ResponseEntity<?> getAvailableBooks() {
        try { List<Book> availableBooks = bookService.findAvailableBooks(); return ResponseEntity.ok(ApiResponse.success("Available books retrieved successfully", availableBooks));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve available books: " + e.getMessage())); }
    }

    @GetMapping("/search") public ResponseEntity<?> searchBooks(@RequestParam String keyword) {
        try { List<Book> books = bookService.searchBooks(keyword); return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", books));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to search books: " + e.getMessage())); }
    }

    @GetMapping("/count") public ResponseEntity<?> getBookCounts() {
        try { Long totalBooks = bookService.getTotalBookCount(); Long availableBooks = bookService.getAvailableBookCount();
            Map<String, Long> counts = Map.of("totalBooks", totalBooks, "availableBooks", availableBooks, "borrowedBooks", totalBooks - availableBooks);
            return ResponseEntity.ok(ApiResponse.success("Book counts retrieved successfully", counts));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve book counts: " + e.getMessage())); }
    }

    @PatchMapping("/{id}/copies") public ResponseEntity<?> updateBookCopies(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try { Integer newTotalCopies = request.get("totalCopies"); if (newTotalCopies == null) return ResponseEntity.badRequest().body(ApiResponse.error("Total copies is required"));
            Book updatedBook = bookService.updateBookCopies(id, newTotalCopies); return ResponseEntity.ok(ApiResponse.success("Book copies updated successfully", updatedBook));
        } catch (Exception e) { return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update book copies: " + e.getMessage())); }
    }
}