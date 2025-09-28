package com.library.repository;

import com.library.entity.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    List<BorrowRecord> findByPatronIdAndStatus(Long patronId, BorrowRecord.BorrowStatus status);
    
    List<BorrowRecord> findByBookIdAndStatus(Long bookId, BorrowRecord.BorrowStatus status);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < :currentDate")
    List<BorrowRecord> findOverdueRecords(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.patron.id = :patronId")
    Page<BorrowRecord> findByPatronId(@Param("patronId") Long patronId, Pageable pageable);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.book.id = :bookId")
    Page<BorrowRecord> findByBookId(@Param("bookId") Long bookId, Pageable pageable);
    
    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate BETWEEN :startDate AND :endDate")
    List<BorrowRecord> findRecordsDueBetween(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.borrowDate = :date")
    long countBorrowsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.returnDate = :date")
    long countReturnsByDate(@Param("date") LocalDate date);
}