package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.entity.Category;
import com.library.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('LIBRARIAN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    private final CategoryService categoryService;
    
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody Category category) {
        logger.info("Creating new category: {}", category.getName());
        
        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", savedCategory));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        logger.info("Fetching category with ID: {}", id);
        
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        logger.info("Fetching all categories");
        
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id, 
                                                              @Valid @RequestBody Category category) {
        logger.info("Updating category with ID: {}", id);
        
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updatedCategory));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        logger.info("Deleting category with ID: {}", id);
        
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
