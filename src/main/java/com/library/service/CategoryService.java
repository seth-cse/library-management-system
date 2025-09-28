package com.library.service;

import com.library.entity.Category;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public Category createCategory(Category category) {
        logger.info("Creating category: {}", category.getName());
        
        // Set creation timestamp
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }
    
    public Category findById(Long id) {
        logger.info("Finding category by ID: {}", id);
        
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }
    
    public List<Category> findAll() {
        logger.info("Fetching all categories");
        
        return categoryRepository.findAll();
    }
    
    public Category updateCategory(Long id, Category categoryDetails) {
        logger.info("Updating category with ID: {}", id);
        
        Category existingCategory = findById(id);
        
        // Update fields
        existingCategory.setName(categoryDetails.getName());
        existingCategory.setDescription(categoryDetails.getDescription());
        existingCategory.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(existingCategory);
    }
    
    public void deleteCategory(Long id) {
        logger.info("Deleting category with ID: {}", id);
        
        Category category = findById(id);
        categoryRepository.delete(category);
    }
    
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
    
    public List<Category> findByNameContaining(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
}