package com.library.dto.response;

import com.library.entity.Book;

import java.util.List;
import java.util.stream.Collectors;

public class BookResponse {
    
    private Long id;
    private String isbn;
    private String title;
    private String description;
    private String categoryName;
    private List<String> authors;
    private Integer publicationYear;
    private String publisher;
    private Integer totalCopies;
    private Integer availableCopies;
    private String location;
    private String status;
    
    // Constructor from entity
    public BookResponse(Book book) {
        this.id = book.getId();
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.description = book.getDescription();
        this.categoryName = book.getCategory().getName();
        this.authors = book.getAuthors().stream()
                .map(author -> author.getName())
                .collect(Collectors.toList());
        this.publicationYear = book.getPublicationYear();
        this.publisher = book.getPublisher();
        this.totalCopies = book.getTotalCopies();
        this.availableCopies = book.getAvailableCopies();
        this.location = book.getLocation();
        this.status = book.getStatus().toString();
    }
    
    // Default constructor
    public BookResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }
    
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
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
