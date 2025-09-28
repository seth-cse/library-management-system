package com.library.service;

import com.library.entity.Author;
import com.library.exception.ResourceAlreadyExistsException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthorService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);
    
    private final AuthorRepository authorRepository;
    
    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
    
    public Author createAuthor(Author author) {
        logger.info("Creating new author: {}", author.getName());
        
        if (authorRepository.existsByName(author.getName())) {
            throw new ResourceAlreadyExistsException("Author already exists: " + author.getName());
        }
        
        Author savedAuthor = authorRepository.save(author);
        logger.info("Author created successfully with ID: {}", savedAuthor.getId());
        
        return savedAuthor;
    }
    
    @Transactional(readOnly = true)
    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Author> findAll() {
        return authorRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Author> searchByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }
    
    public Author updateAuthor(Long id, Author updatedAuthor) {
        logger.info("Updating author with ID: {}", id);
        
        Author existingAuthor = findById(id);
        
        if (!existingAuthor.getName().equals(updatedAuthor.getName()) &&
            authorRepository.existsByName(updatedAuthor.getName())) {
            throw new ResourceAlreadyExistsException("Author already exists: " + updatedAuthor.getName());
        }
        
        existingAuthor.setName(updatedAuthor.getName());
        existingAuthor.setBiography(updatedAuthor.getBiography());
        
        Author savedAuthor = authorRepository.save(existingAuthor);
        logger.info("Author updated successfully with ID: {}", savedAuthor.getId());
        
        return savedAuthor;
    }
    
    public void deleteAuthor(Long id) {
        logger.info("Deleting author with ID: {}", id);
        
        Author author = findById(id);
        
        if (!author.getBooks().isEmpty()) {
            throw new IllegalStateException("Cannot delete author with associated books");
        }
        
        authorRepository.delete(author);
        logger.info("Author deleted successfully with ID: {}", id);
    }
}