package com.library.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    
    private boolean success = false;
    private String message;
    private String error;
    private List<String> details;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String message) {
        this();
        this.message = message;
    }
    
    public ErrorResponse(String message, String error) {
        this();
        this.message = message;
        this.error = error;
    }
    
    public ErrorResponse(String message, List<String> details) {
        this();
        this.message = message;
        this.details = details;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
