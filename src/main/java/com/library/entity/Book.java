package com.library.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$", 
             message = "Invalid ISBN format")
    private String isbn;
    
    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors = new ArrayList<>();
    
    @Column(name = "publication_year")
    @Min(value = 1000, message = "Publication year must be valid")
    @Max(value = 2030, message = "Publication year cannot be in the future")
    private Integer publicationYear;
    
    @Column(nullable = false)
    @NotBlank(message = "Publisher is required")
    @Size(max = 100, message = "Publisher name must not exceed 100 characters")
    private String publisher;
    
    @Column(name = "total_copies", nullable = false)
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;
    
    @Column(name = "available_copies", nullable = false)
    @Min(value = 0, message = "Available copies cannot be negative")
    private Integer availableCopies;
    
    @Column(nullable = false)
    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status = BookStatus.AVAILABLE;
    
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();
    
    // Constructors
    public Book() {}
    
    public Book(String isbn, String title, String description, Category category, 
                Integer publicationYear, String publisher, Integer totalCopies, String location) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.category = category;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.location = location;
    }
    
    // Business methods
    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE && availableCopies > 0;
    }
    
    public boolean canBeBorrowed() {
        return isAvailable() && !category.isRareCategory();
    }
    
    public void borrowCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            if (availableCopies == 0) {
                status = BookStatus.BORROWED;
            }
        }
    }
    
    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            if (status == BookStatus.BORROWED) {
                status = BookStatus.AVAILABLE;
            }
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public List<Author> getAuthors() { return authors; }
    public void setAuthors(List<Author> authors) { this.authors = authors; }
    
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    
    public List<BorrowRecord> getBorrowRecords() { return borrowRecords; }
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { this.borrowRecords = borrowRecords; }
    
    public enum BookStatus {
        AVAILABLE, BORROWED, MAINTENANCE
    }
}