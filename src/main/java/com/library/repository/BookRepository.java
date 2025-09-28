package com.library.repository;

import com.library.entity.Book;
import com.library.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByIsbn(String isbn);
    
    boolean existsByIsbn(String isbn);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByCategory(Category category);
    
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.name LIKE %:authorName%")
    List<Book> findByAuthorNameContainingIgnoreCase(@Param("authorName") String authorName);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
           "(:authorName IS NULL OR EXISTS (SELECT a FROM b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%')))) AND " +
           "(:publisher IS NULL OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :publisher, '%'))) AND " +
           "(:isbn IS NULL OR b.isbn = :isbn)")
    Page<Book> findBooksWithFilters(@Param("title") String title,
                                   @Param("categoryId") Long categoryId,
                                   @Param("authorName") String authorName,
                                   @Param("publisher") String publisher,
                                   @Param("isbn") String isbn,
                                   Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAvailableBooks();
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.category.id = :categoryId")
    long countBooksByCategory(@Param("categoryId") Long categoryId);
}