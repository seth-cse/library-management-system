package com.library.service;

import com.library.dto.request.BorrowRequest;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.Patron;
import com.library.exception.BusinessRuleException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.PatronRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BorrowService {
    
    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);
    private static final int BORROW_PERIOD_DAYS = 14;
    private static final int MAX_BORROWED_BOOKS = 5;
    
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final EmailService emailService;
    private final AuditService auditService;
    
    @Autowired
    public BorrowService(BorrowRecordRepository borrowRecordRepository, BookRepository bookRepository,
                        PatronRepository patronRepository, EmailService emailService, AuditService auditService) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.patronRepository = patronRepository;
        this.emailService = emailService;
        this.auditService = auditService;
    }
    
    public BorrowRecord borrowBook(BorrowRequest request) {
        logger.info("Processing borrow request for book ID: {} by patron ID: {}", 
                   request.getBookId(), request.getPatronId());
        
        // Validate entities exist
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.getBookId()));
        
        Patron patron = patronRepository.findById(request.getPatronId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with ID: " + request.getPatronId()));
        
        // Apply business rules
        validateBorrowingRules(book, patron);
        
        // Create borrow record
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BORROW_PERIOD_DAYS);
        
        BorrowRecord borrowRecord = new BorrowRecord(book, patron, borrowDate, dueDate);
        
        // Update book availability
        book.borrowCopy();
        bookRepository.save(book);
        
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        
        // Log audit
        auditService.logBorrowOperation(patron.getUser().getUsername(), book.getTitle(), "BORROW");
        
        // Send confirmation email
        emailService.sendBorrowConfirmation(patron.getUser().getEmail(), book.getTitle(), dueDate);
        
        logger.info("Book borrowed successfully. Record ID: {}", savedRecord.getId());
        
        return savedRecord;
    }
    
    public BorrowRecord returnBook(Long borrowRecordId) {
        logger.info("Processing return for borrow record ID: {}", borrowRecordId);
        
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with ID: " + borrowRecordId));
        
        if (borrowRecord.getStatus() != BorrowRecord.BorrowStatus.BORROWED) {
            throw new BusinessRuleException("Book has already been returned");
        }
        
        // Process return
        borrowRecord.returnBook();
        
        // Update book availability
        Book book = borrowRecord.getBook();
        book.returnCopy();
        bookRepository.save(book);
        
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        
        // Log audit
        auditService.logBorrowOperation(borrowRecord.getPatron().getUser().getUsername(), 
                                      book.getTitle(), "RETURN");
        
        // Send return confirmation if there was a fine
        if (borrowRecord.getFineAmount() != null && borrowRecord.getFineAmount() > 0) {
            emailService.sendFineNotification(borrowRecord.getPatron().getUser().getEmail(), 
                                            book.getTitle(), borrowRecord.getFineAmount());
        }
        
        logger.info("Book returned successfully. Record ID: {}", savedRecord.getId());
        
        return savedRecord;
    }
    
    @Transactional(readOnly = true)
    public BorrowRecord findById(Long id) {
        return borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<BorrowRecord> findByPatronId(Long patronId) {
        return borrowRecordRepository.findByPatronIdAndStatus(patronId, BorrowRecord.BorrowStatus.BORROWED);
    }
    
    @Transactional(readOnly = true)
    public Page<BorrowRecord> findBorrowHistory(Long patronId, Pageable pageable) {
        return borrowRecordRepository.findByPatronId(patronId, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<BorrowRecord> findOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now());
    }
    
    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM
    public void processOverdueBooks() {
        logger.info("Processing overdue books...");
        
        List<BorrowRecord> overdueRecords = findOverdueRecords();
        
        for (BorrowRecord record : overdueRecords) {
            if (record.getStatus() == BorrowRecord.BorrowStatus.BORROWED) {
                record.setStatus(BorrowRecord.BorrowStatus.OVERDUE);
                record.calculateFine();
                borrowRecordRepository.save(record);
                
                // Send overdue notification
                emailService.sendOverdueNotification(
                    record.getPatron().getUser().getEmail(),
                    record.getBook().getTitle(),
                    record.getDaysOverdue(),
                    record.getFineAmount()
                );
            }
        }
        
        logger.info("Processed {} overdue records", overdueRecords.size());
    }
    
    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM
    public void sendDueDateReminders() {
        logger.info("Sending due date reminders...");
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);
        
        List<BorrowRecord> dueSoon = borrowRecordRepository.findRecordsDueBetween(tomorrow, dayAfterTomorrow);
        
        for (BorrowRecord record : dueSoon) {
            if (record.getStatus() == BorrowRecord.BorrowStatus.BORROWED) {
                emailService.sendDueDateReminder(
                    record.getPatron().getUser().getEmail(),
                    record.getBook().getTitle(),
                    record.getDueDate()
                );
            }
        }
        
        logger.info("Sent {} due date reminders", dueSoon.size());
    }
    
    private void validateBorrowingRules(Book book, Patron patron) {
        // Rule 1: Book must be available and borrowable
        if (!book.canBeBorrowed()) {
            if (book.getCategory().isRareCategory()) {
                throw new BusinessRuleException("Rare category books cannot be borrowed");
            } else {
                throw new BusinessRuleException("Book is not available for borrowing");
            }
        }
        
        // Rule 2: Patron must be active
        if (patron.getStatus() != Patron.PatronStatus.ACTIVE) {
            throw new BusinessRuleException("Patron account is not active");
        }
        
        // Rule 3: Patron cannot have overdue books
        if (patron.hasOverdueBooks()) {
            throw new BusinessRuleException("Patron has overdue books and cannot borrow new books");
        }
        
        // Rule 4: Patron cannot exceed maximum borrowed books
        if (patron.getCurrentBorrowedBooksCount() >= MAX_BORROWED_BOOKS) {
            throw new BusinessRuleException("Patron has reached maximum borrowed books limit (" + MAX_BORROWED_BOOKS + ")");
        }
    }
}
