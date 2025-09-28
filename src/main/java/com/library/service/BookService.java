package com.library.service;

import com.library.dto.request.BookRequest;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.exception.ResourceAlreadyExistsException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    
    @Autowired
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository, 
                      AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }
    
    public Book createBook(BookRequest request) {
        logger.info("Creating new book with ISBN: {}", request.getIsbn());
        
        // Validate ISBN uniqueness
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new ResourceAlreadyExistsException("Book with ISBN already exists: " + request.getIsbn());
        }
        
        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
        
        // Get authors
        List<Author> authors = authorRepository.findAllById(request.getAuthorIds());
        if (authors.size() != request.getAuthorIds().size()) {
            throw new ResourceNotFoundException("One or more authors not found");
        }
        
        // Create book
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setCategory(category);
        book.setAuthors(authors);
        book.setPublicationYear(request.getPublicationYear());
        book.setPublisher(request.getPublisher());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setLocation(request.getLocation());
        
        Book savedBook = bookRepository.save(book);
        logger.info("Book created successfully with ID: {}", savedBook.getId());
        
        return savedBook;
    }
    
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
    }
    
    @Transactional(readOnly = true)
    public Page<Book> findBooksWithFilters(String title, Long categoryId, String authorName, 
                                         String publisher, String isbn, Pageable pageable) {
        return bookRepository.findBooksWithFilters(title, categoryId, authorName, publisher, isbn, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Book> findAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }
    
    @Transactional(readOnly = true)
    public List<Book> findByTitleContaining(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    @Transactional(readOnly = true)
    public List<Book> findByAuthorName(String authorName) {
        return bookRepository.findByAuthorNameContainingIgnoreCase(authorName);
    }
    
    @Transactional(readOnly = true)
    public List<Book> findByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        return bookRepository.findByCategory(category);
    }
    
    public Book updateBook(Long id, BookRequest request) {
        logger.info("Updating book with ID: {}", id);
        
        Book existingBook = findById(id);
        
        // Check ISBN uniqueness if changed
        if (!existingBook.getIsbn().equals(request.getIsbn()) && 
            bookRepository.existsByIsbn(request.getIsbn())) {
            throw new ResourceAlreadyExistsException("Book with ISBN already exists: " + request.getIsbn());
        }
        
        // Update fields
        existingBook.setIsbn(request.getIsbn());
        existingBook.setTitle(request.getTitle());
        existingBook.setDescription(request.getDescription());
        existingBook.setPublicationYear(request.getPublicationYear());
        existingBook.setPublisher(request.getPublisher());
        existingBook.setLocation(request.getLocation());
        
        // Update total copies and adjust available copies proportionally
        if (!existingBook.getTotalCopies().equals(request.getTotalCopies())) {
            int difference = request.getTotalCopies() - existingBook.getTotalCopies();
            existingBook.setTotalCopies(request.getTotalCopies());
            existingBook.setAvailableCopies(Math.max(0, existingBook.getAvailableCopies() + difference));
        }
        
        // Update category if changed
        if (!existingBook.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
            existingBook.setCategory(category);
        }
        
        // Update authors if changed
        List<Author> authors = authorRepository.findAllById(request.getAuthorIds());
        if (authors.size() != request.getAuthorIds().size()) {
            throw new ResourceNotFoundException("One or more authors not found");
        }
        existingBook.setAuthors(authors);
        
        Book savedBook = bookRepository.save(existingBook);
        logger.info("Book updated successfully with ID: {}", savedBook.getId());
        
        return savedBook;
    }
    
    public void deleteBook(Long id) {
        logger.info("Deleting book with ID: {}", id);
        
        Book book = findById(id);
        
        // Check if book has active borrows
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new IllegalStateException("Cannot delete book with active borrows");
        }
        
        bookRepository.delete(book);
        logger.info("Book deleted successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public long countBooksByCategory(Long categoryId) {
        return bookRepository.countBooksByCategory(categoryId);
    }
}