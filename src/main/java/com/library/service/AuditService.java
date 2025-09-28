package com.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuditService {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Async
    public void logBorrowOperation(String username, String bookTitle, String operation) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String auditMessage = String.format(
            "[%s] User: %s | Operation: %s | Book: %s", 
            timestamp, username, operation, bookTitle
        );
        
        auditLogger.info(auditMessage);
    }
    
    @Async
    public void logUserOperation(String username, String operation, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String auditMessage = String.format(
            "[%s] User: %s | Operation: %s | Details: %s", 
            timestamp, username, operation, details
        );
        
        auditLogger.info(auditMessage);
    }
    
    @Async
    public void logSecurityEvent(String username, String event, String ipAddress) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String auditMessage = String.format(
            "[%s] Security Event: %s | User: %s | IP: %s", 
            timestamp, event, username, ipAddress
        );
        
        auditLogger.warn(auditMessage);
    }
}