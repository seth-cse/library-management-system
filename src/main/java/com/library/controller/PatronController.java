package com.library.controller;

import com.library.dto.request.PatronRequest;
import com.library.dto.response.ApiResponse;
import com.library.entity.Patron;
import com.library.service.PatronService;
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
@RequestMapping("/api/patrons")
@PreAuthorize("hasRole('LIBRARIAN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatronController {
    
    private static final Logger logger = LoggerFactory.getLogger(PatronController.class);
    
    private final PatronService patronService;
    
    @Autowired
    public PatronController(PatronService patronService) {
        this.patronService = patronService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Patron>> createPatron(@Valid @RequestBody PatronRequest patronRequest) {
        logger.info("Creating new patron for user ID: {}", patronRequest.getUserId());
        
        Patron patron = patronService.createPatron(patronRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patron created successfully", patron));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Patron>> getPatronById(@PathVariable Long id) {
        logger.info("Fetching patron with ID: {}", id);
        
        Patron patron = patronService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(patron));
    }
    
    @GetMapping("/card/{libraryCardNumber}")
    public ResponseEntity<ApiResponse<Patron>> getPatronByCardNumber(@PathVariable String libraryCardNumber) {
        logger.info("Fetching patron with card number: {}", libraryCardNumber);
        
        Patron patron = patronService.findByLibraryCardNumber(libraryCardNumber);
        return ResponseEntity.ok(ApiResponse.success(patron));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Patron>>> getAllPatrons() {
        logger.info("Fetching all patrons");
        
        List<Patron> patrons = patronService.findAll();
        return ResponseEntity.ok(ApiResponse.success(patrons));
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<Patron>>> getPatronsWithOverdueBooks() {
        logger.info("Fetching patrons with overdue books");
        
        List<Patron> patrons = patronService.findPatronsWithOverdueBooks();
        return ResponseEntity.ok(ApiResponse.success(patrons));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Patron>> updatePatronStatus(@PathVariable Long id,
                                                                @RequestParam Patron.PatronStatus status) {
        logger.info("Updating patron status for ID: {} to {}", id, status);
        
        Patron patron = patronService.updatePatronStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Patron status updated successfully", patron));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatron(@PathVariable Long id) {
        logger.info("Deleting patron with ID: {}", id);
        
        patronService.deletePatron(id);
        return ResponseEntity.ok(ApiResponse.success("Patron deleted successfully", null));
    }
}