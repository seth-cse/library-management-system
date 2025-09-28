package com.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "borrow_records")
public class BorrowRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Book is required")
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patron_id", nullable = false)
    @NotNull(message = "Patron is required")
    private Patron patron;
    
    @Column(name = "borrow_date", nullable = false)
    @NotNull(message = "Borrow date is required")
    private LocalDate borrowDate;
    
    @Column(name = "due_date", nullable = false)
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status = BorrowStatus.BORROWED;
    
    @Column(name = "fine_amount")
    private Double fineAmount;
    
    // Constructors
    public BorrowRecord() {}
    
    public BorrowRecord(Book book, Patron patron, LocalDate borrowDate, LocalDate dueDate) {
        this.book = book;
        this.patron = patron;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }
    
    // Business methods
    public boolean isOverdue() {
        return status == BorrowStatus.BORROWED && LocalDate.now().isAfter(dueDate);
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    public void calculateFine() {
        if (isOverdue()) {
            long daysOverdue = getDaysOverdue();
            fineAmount = daysOverdue * 1.0; // $1 per day
        }
    }
    
    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.status = BorrowStatus.RETURNED;
        if (isOverdue()) {
            calculateFine();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    
    public Patron getPatron() { return patron; }
    public void setPatron(Patron patron) { this.patron = patron; }
    
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public BorrowStatus getStatus() { return status; }
    public void setStatus(BorrowStatus status) { this.status = status; }
    
    public Double getFineAmount() { return fineAmount; }
    public void setFineAmount(Double fineAmount) { this.fineAmount = fineAmount; }
    
    public enum BorrowStatus {
        BORROWED, RETURNED, OVERDUE
    }
}