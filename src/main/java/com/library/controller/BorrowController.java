package com.library.controller;

import com.library.dto.request.BorrowRequest;
import com.library.dto.response.ApiResponse;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;
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
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BorrowController {
    
    private static final Logger logger = LoggerFactory.getLogger(BorrowController.class);
    
    private final BorrowService borrowService;
    
    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('PATRON')")
    public ResponseEntity<ApiResponse<BorrowRecord>> borrowBook(@Valid @RequestBody BorrowRequest borrowRequest) {
        logger.info("Processing borrow request for book ID: {} by patron ID: {}", 
                   borrowRequest.getBookId(), borrowRequest.getPatronId());
        
        BorrowRecord borrowRecord = borrowService.borrowBook(borrowRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book borrowed successfully", borrowRecord));
    }
    
    @PutMapping("/{id}/return")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('PATRON')")
    public ResponseEntity<ApiResponse<BorrowRecord>> returnBook(@PathVariable Long id) {
        logger.info("Processing return for borrow record ID: {}", id);
        
        BorrowRecord borrowRecord = borrowService.returnBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", borrowRecord));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<BorrowRecord>> getBorrowRecordById(@PathVariable Long id) {
        logger.info("Fetching borrow record with ID: {}", id);
        
        BorrowRecord borrowRecord = borrowService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(borrowRecord));
    }
    
    @GetMapping("/patron/{patronId}")
    @PreAuthorize("hasRole('LIBRARIAN') or @patronService.findById(#patronId).user.username == authentication.name")
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getCurrentBorrowsByPatron(@PathVariable Long patronId) {
        logger.info("Fetching current borrows for patron ID: {}", patronId);
        
        List<BorrowRecord> borrowRecords = borrowService.findByPatronId(patronId);
        return ResponseEntity.ok(ApiResponse.success(borrowRecords));
    }
    
    @GetMapping("/patron/{patronId}/history")
    @PreAuthorize("hasRole('LIBRARIAN') or @patronService.findById(#patronId).user.username == authentication.name")
    public ResponseEntity<ApiResponse<Page<BorrowRecord>>> getBorrowHistory(
            @PathVariable Long patronId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        logger.info("Fetching borrow history for patron ID: {}", patronId);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BorrowRecord> borrowHistory = borrowService.findBorrowHistory(patronId, pageable);
        return ResponseEntity.ok(ApiResponse.success(borrowHistory));
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getOverdueRecords() {
        logger.info("Fetching overdue records");
        
        List<BorrowRecord> overdueRecords = borrowService.findOverdueRecords();
        return ResponseEntity.ok(ApiResponse.success(overdueRecords));
    }
}