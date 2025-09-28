package com.library.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Author name is required")
    @Size(max = 100, message = "Author name must not exceed 100 characters")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Biography must not exceed 1000 characters")
    private String biography;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "authors")
    private List<Book> books = new ArrayList<>();
    
    // Constructors
    public Author() {}
    
    public Author(String name, String biography) {
        this.name = name;
        this.biography = biography;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author author = (Author) o;
        return id != null && id.equals(author.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
