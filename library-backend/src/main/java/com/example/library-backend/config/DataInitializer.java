package com.example.library-backend.config;

import com.example.library-backend.entity.Book;
import com.example.library-backend.entity.User;
import com.example.library-backend.repository.BookRepository;
import com.example.library-backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("=== STARTING DATA INITIALIZATION ===");
        initializeUsers();
        initializeBooks();
        logger.info("=== DATA INITIALIZATION COMPLETED ===");
    }

    private void initializeUsers() {
        logger.info("Initializing users...");
        
        if (userRepository.count() == 0) {
            logger.info("No users found in database. Creating default users...");
            
            List<User> defaultUsers = Arrays.asList(
                createUser("admin", "admin123", "admin@library.com", "System Administrator", "ROLE_ADMIN", false),
                createUser("librarian", "lib123", "librarian@library.com", "Library Manager", "ROLE_LIBRARIAN", false),
                createUser("johnsmith", "Lib@a1b2c3d4", "john.smith@example.com", "John Smith", "ROLE_USER", true),
                createUser("sarajones", "Lib@e5f6g7h8", "sara.jones@example.com", "Sara Jones", "ROLE_USER", true)
            );

            userRepository.saveAll(defaultUsers);
            logger.info("Created {} default users", defaultUsers.size());
            
            // Log the created users for verification
            for (User user : defaultUsers) {
                logger.info("Created user: {} with role: {}", user.getUsername(), user.getRole());
            }
        } else {
            logger.info("Users already exist in database. Count: {}", userRepository.count());
            
            // Log existing users
            List<User> existingUsers = userRepository.findAll();
            for (User user : existingUsers) {
                logger.info("Existing user: {} with role: {} (active: {})", 
                    user.getUsername(), user.getRole(), user.getActive());
            }
        }
    }

    private User createUser(String username, String password, String email, String fullName, String role, Boolean systemGenerated) {
        String encodedPassword = passwordEncoder.encode(password);
        logger.info("Creating user: {} with password: {} -> encoded: {}", username, password, encodedPassword);
        
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .fullName(fullName)
                .role(role)
                .active(true)
                .systemGenerated(systemGenerated)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void initializeBooks() {
        logger.info("Initializing books...");
        
        if (bookRepository.count() == 0) {
            List<Book> sampleBooks = Arrays.asList(
                createBook("The Great Gatsby", "F. Scott Fitzgerald", "978-0-7432-7356-5", 
                          "Scribner", 1925, "Classic", "A classic novel of the Jazz Age", 5),
                createBook("To Kill a Mockingbird", "Harper Lee", "978-0-06-112008-4", 
                          "J.B. Lippincott & Co.", 1960, "Fiction", "A novel about racial inequality", 3),
                createBook("1984", "George Orwell", "978-0-452-28423-4", 
                          "Secker & Warburg", 1949, "Dystopian", "A dystopian social science fiction novel", 4)
            );

            bookRepository.saveAll(sampleBooks);
            logger.info("Created {} sample books", sampleBooks.size());
        } else {
            logger.info("Books already exist in database. Count: {}", bookRepository.count());
        }
    }

    private Book createBook(String title, String author, String isbn, String publisher, 
                           Integer publicationYear, String genre, String description, Integer totalCopies) {
        return Book.builder()
                .title(title)
                .author(author)
                .isbn(isbn)
                .publisher(publisher)
                .publicationYear(publicationYear)
                .genre(genre)
                .description(description)
                .totalCopies(totalCopies)
                .availableCopies(totalCopies)
                .active(true)
                .build();
    }
}