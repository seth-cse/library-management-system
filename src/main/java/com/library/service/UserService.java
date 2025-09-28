package com.library.service;

import com.library.dto.request.RegisterRequest;
import com.library.entity.User;
import com.library.exception.ResourceAlreadyExistsException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User createUser(RegisterRequest request) {
        logger.info("Creating new user with username: {}", request.getUsername());
        
        // Validate uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        
        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        
        return savedUser;
    }
    
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
    
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public User updateUser(Long id, User updatedUser) {
        logger.info("Updating user with ID: {}", id);
        
        User existingUser = findById(id);
        
        // Update fields (excluding password and username for security)
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new ResourceAlreadyExistsException("Email already exists: " + updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        
        User savedUser = userRepository.save(existingUser);
        logger.info("User updated successfully with ID: {}", savedUser.getId());
        
        return savedUser;
    }
    
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        User user = findById(id);
        userRepository.delete(user);
        
        logger.info("User deleted successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
