package com.library.controller;

import com.library.dto.request.BookRequest;
import com.library.dto.response.ApiResponse;
import com.library.entity.Book;
import com.library.service.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    
    private final BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Book>> createBook(@Valid @RequestBody BookRequest bookRequest) {
        logger.info("Creating new book with ISBN: {}", bookRequest.getIsbn());
        
        Book book = bookService.createBook(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book created successfully", book));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable Long id) {
        logger.info("Fetching book with ID: {}", id);
        
        Book book = bookService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(book));
    }
    
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<ApiResponse<Book>> getBookByIsbn(@PathVariable String isbn) {
        logger.info("Fetching book with ISBN: {}", isbn);
        
        Book book = bookService.findByIsbn(isbn);
        return ResponseEntity.ok(ApiResponse.success(book));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks() {
        logger.info("Fetching all books");
        
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Book>>> getAvailableBooks() {
        logger.info("Fetching available books");
        
        List<Book> books = bookService.findAvailableBooks();
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Book>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String isbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        logger.info("Searching books with filters - title: {}, categoryId: {}, authorName: {}", 
                   title, categoryId, authorName);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> books = bookService.findBooksWithFilters(title, categoryId, authorName, 
                                                           publisher, isbn, pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByCategory(@PathVariable Long categoryId) {
        logger.info("Fetching books by category ID: {}", categoryId);
        
        List<Book> books = bookService.findByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/author")
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByAuthor(@RequestParam String authorName) {
        logger.info("Fetching books by author: {}", authorName);
        
        List<Book> books = bookService.findByAuthorName(authorName);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable Long id, 
                                                       @Valid @RequestBody BookRequest bookRequest) {
        logger.info("Updating book with ID: {}", id);
        
        Book book = bookService.updateBook(id, bookRequest);
        return ResponseEntity.ok(ApiResponse.success("Book updated successfully", book));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        logger.info("Deleting book with ID: {}", id);
        
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }
}