package com.library.dto.request;

import jakarta.validation.constraints.NotNull;

public class PatronRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    // Constructors
    public PatronRequest() {}
    
    public PatronRequest(Long userId) {
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}