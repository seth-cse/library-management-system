package com.library.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patrons")
public class Patron {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "library_card_number", unique = true, nullable = false)
    @NotBlank(message = "Library card number is required")
    @Pattern(regexp = "^LC\\d{6}$", message = "Library card number must follow format LC######")
    private String libraryCardNumber;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @Column(name = "membership_date", nullable = false)
    @NotNull(message = "Membership date is required")
    private LocalDate membershipDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatronStatus status = PatronStatus.ACTIVE;
    
    @JsonIgnore
    @OneToMany(mappedBy = "patron", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();
    
    // Constructors
    public Patron() {}
    
    public Patron(String libraryCardNumber, User user, LocalDate membershipDate) {
        this.libraryCardNumber = libraryCardNumber;
        this.user = user;
        this.membershipDate = membershipDate;
    }
    
    // Business methods
    public boolean canBorrowBooks() {
        return status == PatronStatus.ACTIVE && !hasOverdueBooks() && getCurrentBorrowedBooksCount() < 5;
    }
    
    public int getCurrentBorrowedBooksCount() {
        return (int) borrowRecords.stream()
                .filter(record -> record.getStatus() == BorrowRecord.BorrowStatus.BORROWED)
                .count();
    }
    
    public boolean hasOverdueBooks() {
        return borrowRecords.stream()
                .anyMatch(record -> record.getStatus() == BorrowRecord.BorrowStatus.OVERDUE);
    }
    
    public double getTotalFines() {
        return borrowRecords.stream()
                .filter(record -> record.getFineAmount() != null)
                .mapToDouble(BorrowRecord::getFineAmount)
                .sum();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getLibraryCardNumber() { return libraryCardNumber; }
    public void setLibraryCardNumber(String libraryCardNumber) { this.libraryCardNumber = libraryCardNumber; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDate getMembershipDate() { return membershipDate; }
    public void setMembershipDate(LocalDate membershipDate) { this.membershipDate = membershipDate; }
    
    public PatronStatus getStatus() { return status; }
    public void setStatus(PatronStatus status) { this.status = status; }
    
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
    
    public enum PatronStatus {
        ACTIVE, SUSPENDED, INACTIVE
    }
}
