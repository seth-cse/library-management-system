package com.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Async
    public void sendBorrowConfirmation(String email, String bookTitle, LocalDate dueDate) {
        logger.info("Sending borrow confirmation email to: {} for book: {}", email, bookTitle);
        
        // Mock email sending - in real implementation, use JavaMailSender
        String subject = "Book Borrowed Successfully";
        String message = String.format(
            "Dear Patron,\n\n" +
            "You have successfully borrowed '%s'.\n" +
            "Due Date: %s\n\n" +
            "Please return the book on or before the due date to avoid late fees.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            bookTitle, dueDate.format(DATE_FORMATTER)
        );
        
        // Simulate email sending delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Email sent successfully to: {}", email);
    }
    
    @Async
    public void sendDueDateReminder(String email, String bookTitle, LocalDate dueDate) {
        logger.info("Sending due date reminder email to: {} for book: {}", email, bookTitle);
        
        String subject = "Book Due Date Reminder";
        String message = String.format(
            "Dear Patron,\n\n" +
            "This is a friendly reminder that '%s' is due on %s.\n" +
            "Please return the book on time to avoid late fees ($1 per day).\n\n" +
            "Best regards,\n" +
            "Library Management System",
            bookTitle, dueDate.format(DATE_FORMATTER)
        );
        
        logger.info("Due date reminder sent successfully to: {}", email);
    }
    
    @Async
    public void sendOverdueNotification(String email, String bookTitle, long daysOverdue, Double fineAmount) {
        logger.info("Sending overdue notification email to: {} for book: {}", email, bookTitle);
        
        String subject = "Overdue Book Notice";
        String message = String.format(
            "Dear Patron,\n\n" +
            "Your borrowed book '%s' is %d day(s) overdue.\n" +
            "Current fine amount: $%.2f\n\n" +
            "Please return the book immediately to prevent additional charges.\n" +
            "You cannot borrow new books until all overdue items are returned.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            bookTitle, daysOverdue, fineAmount
        );
        
        logger.info("Overdue notification sent successfully to: {}", email);
    }
    
    @Async
    public void sendFineNotification(String email, String bookTitle, Double fineAmount) {
        logger.info("Sending fine notification email to: {} for book: {}", email, bookTitle);
        
        String subject = "Late Return Fine Notice";
        String message = String.format(
            "Dear Patron,\n\n" +
            "You have returned '%s' late.\n" +
            "Fine amount: $%.2f\n\n" +
            "Please pay this fine at the library desk.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            bookTitle, fineAmount
        );
        
        logger.info("Fine notification sent successfully to: {}", email);
    }
}
