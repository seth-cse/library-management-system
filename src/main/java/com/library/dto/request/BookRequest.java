package com.library.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public class BookRequest {
    
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$", 
             message = "Invalid ISBN format")
    private String isbn;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @NotEmpty(message = "At least one author is required")
    private List<Long> authorIds;
    
    @Min(value = 1000, message = "Publication year must be valid")
    @Max(value = 2030, message = "Publication year cannot be in the future")
    private Integer publicationYear;
    
    @NotBlank(message = "Publisher is required")
    @Size(max = 100, message = "Publisher name must not exceed 100 characters")
    private String publisher;
    
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;
    
    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;
    
    // Constructors
    public BookRequest() {}
    
    public BookRequest(String isbn, String title, String description, Long categoryId,
                      List<Long> authorIds, Integer publicationYear, String publisher,
                      Integer totalCopies, String location) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.authorIds = authorIds;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
        this.totalCopies = totalCopies;
        this.location = location;
    }
    
    // Getters and Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public List<Long> getAuthorIds() { return authorIds; }
    public void setAuthorIds(List<Long> authorIds) { this.authorIds = authorIds; }
    
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}