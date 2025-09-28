package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.entity.Author;
import com.library.service.AuthorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@PreAuthorize("hasRole('LIBRARIAN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthorController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);
    
    private final AuthorService authorService;
    
    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Author>> createAuthor(@Valid @RequestBody Author author) {
        logger.info("Creating new author: {}", author.getName());
        
        Author savedAuthor = authorService.createAuthor(author);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Author created successfully", savedAuthor));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Author>> getAuthorById(@PathVariable Long id) {
        logger.info("Fetching author with ID: {}", id);
        
        Author author = authorService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(author));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Author>>> getAllAuthors() {
        logger.info("Fetching all authors");
        
        List<Author> authors = authorService.findAll();
        return ResponseEntity.ok(ApiResponse.success(authors));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Author>>> searchAuthors(@RequestParam String name) {
        logger.info("Searching authors by name: {}", name);
        
        List<Author> authors = authorService.searchByName(name);
        return ResponseEntity.ok(ApiResponse.success(authors));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Author>> updateAuthor(@PathVariable Long id, 
                                                          @Valid @RequestBody Author author) {
        logger.info("Updating author with ID: {}", id);
        
        Author updatedAuthor = authorService.updateAuthor(id, author);
        return ResponseEntity.ok(ApiResponse.success("Author updated successfully", updatedAuthor));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable Long id) {
        logger.info("Deleting author with ID: {}", id);
        
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(ApiResponse.success("Author deleted successfully", null));
    }
}