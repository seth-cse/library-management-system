package com.library.service;

import com.library.dto.request.PatronRequest;
import com.library.entity.Patron;
import com.library.entity.User;
import com.library.exception.ResourceAlreadyExistsException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.PatronRepository;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class PatronService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatronService.class);
    
    private final PatronRepository patronRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public PatronService(PatronRepository patronRepository, UserRepository userRepository) {
        this.patronRepository = patronRepository;
        this.userRepository = userRepository;
    }
    
    public Patron createPatron(PatronRequest request) {
        logger.info("Creating new patron for user ID: {}", request.getUserId());
        
        // Validate user exists and is not already a patron
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
        
        if (patronRepository.findByUserId(user.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Patron already exists for user ID: " + request.getUserId());
        }
        
        // Generate unique library card number
        String libraryCardNumber = generateLibraryCardNumber();
        while (patronRepository.existsByLibraryCardNumber(libraryCardNumber)) {
            libraryCardNumber = generateLibraryCardNumber();
        }
        
        // Create patron
        Patron patron = new Patron();
        patron.setLibraryCardNumber(libraryCardNumber);
        patron.setUser(user);
        patron.setMembershipDate(LocalDate.now());
        patron.setStatus(Patron.PatronStatus.ACTIVE);
        
        Patron savedPatron = patronRepository.save(patron);
        logger.info("Patron created successfully with ID: {} and card number: {}", 
                   savedPatron.getId(), savedPatron.getLibraryCardNumber());
        
        return savedPatron;
    }
    
    @Transactional(readOnly = true)
    public Patron findById(Long id) {
        logger.info("Finding patron by ID: {}", id);
        
        return patronRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Patron findByUserId(Long userId) {
        return patronRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found for user ID: " + userId));
    }
    
    @Transactional(readOnly = true)
    public Patron findByLibraryCardNumber(String libraryCardNumber) {
        logger.info("Finding patron by library card number: {}", libraryCardNumber);
        
        return patronRepository.findByLibraryCardNumber(libraryCardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with library card number: " + libraryCardNumber));
    }
    
    @Transactional(readOnly = true)
    public List<Patron> findAll() {
        return patronRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Patron> findPatronsWithOverdueBooks() {
        return patronRepository.findPatronsWithOverdueBooks();
    }
    
    public Patron updatePatronStatus(Long id, Patron.PatronStatus status) {
        logger.info("Updating patron status for ID: {} to {}", id, status);
        
        Patron patron = findById(id);
        patron.setStatus(status);
        
        Patron savedPatron = patronRepository.save(patron);
        logger.info("Patron status updated successfully for ID: {}", savedPatron.getId());
        
        return savedPatron;
    }
    
    public void deletePatron(Long id) {
        logger.info("Deleting patron with ID: {}", id);
        
        Patron patron = findById(id);
        
        // Check for active borrows
        long activeBorrows = patronRepository.countCurrentBorrowedBooks(id);
        if (activeBorrows > 0) {
            throw new IllegalStateException("Cannot delete patron with active borrowed books");
        }
        
        patronRepository.delete(patron);
        logger.info("Patron deleted successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public long countActivePatrons() {
        return patronRepository.countByStatus(Patron.PatronStatus.ACTIVE);
    }
    
    @Transactional(readOnly = true)
    public List<Patron> findActivePatrons() {
        return patronRepository.findByStatus(Patron.PatronStatus.ACTIVE);
    }
    
    private String generateLibraryCardNumber() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // 6-digit number
        return "LC" + number;
    }
}