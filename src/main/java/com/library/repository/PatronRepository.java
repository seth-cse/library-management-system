package com.library.repository;

import com.library.entity.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatronRepository extends JpaRepository<Patron, Long> {
    
    Optional<Patron> findByLibraryCardNumber(String libraryCardNumber);
    
    Optional<Patron> findByUserId(Long userId);
    
    boolean existsByLibraryCardNumber(String libraryCardNumber);
    
    // Added missing methods for patron status queries
    long countByStatus(Patron.PatronStatus status);
    
    List<Patron> findByStatus(Patron.PatronStatus status);
    
    @Query("SELECT p FROM Patron p JOIN p.borrowRecords br WHERE br.status = 'OVERDUE'")
    List<Patron> findPatronsWithOverdueBooks();
    
    @Query("SELECT p FROM Patron p WHERE SIZE(p.borrowRecords) >= :maxBooks")
    List<Patron> findPatronsWithMaxBorrowedBooks(@Param("maxBooks") int maxBooks);
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.patron.id = :patronId AND br.status = 'BORROWED'")
    long countCurrentBorrowedBooks(@Param("patronId") Long patronId);
}