package com.example.library-backend.repository;

import com.example.library-backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByGenreContainingIgnoreCase(String genre);
    Optional<Book> findByIsbn(String isbn);
    @Query("SELECT b FROM Book b WHERE b.active = true") List<Book> findByActiveTrue();
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 AND b.active = true") List<Book> findAvailableBooks();
    @Query("SELECT COUNT(b) FROM Book b WHERE b.active = true") Long countActiveBooks();
    @Query("SELECT b FROM Book b WHERE b.id = :id AND b.active = true") Optional<Book> findByIdAndActiveTrue(@Param("id") Long id);
    @Query("SELECT b FROM Book b WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND b.active = true")
    List<Book> searchBooks(@Param("keyword") String keyword);
}