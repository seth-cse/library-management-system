package com.library.dto.response;

import com.library.entity.BorrowRecord;

import java.time.LocalDate;

public class BorrowRecordResponse {
    
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long patronId;
    private String patronLibraryCardNumber;
    private String patronName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private Double fineAmount;
    private Long daysOverdue;
    
    // Constructor from entity
    public BorrowRecordResponse(BorrowRecord borrowRecord) {
        this.id = borrowRecord.getId();
        this.bookId = borrowRecord.getBook().getId();
        this.bookTitle = borrowRecord.getBook().getTitle();
        this.bookIsbn = borrowRecord.getBook().getIsbn();
        this.patronId = borrowRecord.getPatron().getId();
        this.patronLibraryCardNumber = borrowRecord.getPatron().getLibraryCardNumber();
        this.patronName = borrowRecord.getPatron().getUser().getUsername();
        this.borrowDate = borrowRecord.getBorrowDate();
        this.dueDate = borrowRecord.getDueDate();
        this.returnDate = borrowRecord.getReturnDate();
        this.status = borrowRecord.getStatus().toString();
        this.fineAmount = borrowRecord.getFineAmount();
        this.daysOverdue = borrowRecord.getDaysOverdue();
    }
    
    // Default constructor
    public BorrowRecordResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }
    
    public Long getPatronId() { return patronId; }
    public void setPatronId(Long patronId) { this.patronId = patronId; }
    
    public String getPatronLibraryCardNumber() { return patronLibraryCardNumber; }
    public void setPatronLibraryCardNumber(String patronLibraryCardNumber) { 
        this.patronLibraryCardNumber = patronLibraryCardNumber; 
    }
    
    public String getPatronName() { return patronName; }
    public void setPatronName(String patronName) { this.patronName = patronName; }
    
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Double getFineAmount() { return fineAmount; }
    public void setFineAmount(Double fineAmount) { this.fineAmount = fineAmount; }
    
    public Long getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(Long daysOverdue) { this.daysOverdue = daysOverdue; }
}
