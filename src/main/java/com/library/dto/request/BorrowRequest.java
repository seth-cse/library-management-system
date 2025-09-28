package com.library.dto.request;

import jakarta.validation.constraints.NotNull;

public class BorrowRequest {
    
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    @NotNull(message = "Patron ID is required")
    private Long patronId;
    
    // Constructors
    public BorrowRequest() {}
    
    public BorrowRequest(Long bookId, Long patronId) {
        this.bookId = bookId;
        this.patronId = patronId;
    }
    
    // Getters and Setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    
    public Long getPatronId() { return patronId; }
    public void setPatronId(Long patronId) { this.patronId = patronId; }
}