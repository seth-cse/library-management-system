package com.library.controller;

import com.library.dto.request.LoginRequest;
import com.library.dto.request.RegisterRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.JwtResponse;
import com.library.entity.User;
import com.library.security.JwtUtil;
import com.library.service.AuditService;
import com.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600) // Specify exact origin instead of "*"
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;
    
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                         JwtUtil jwtUtil, AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.auditService = auditService;
    }

    // Add a simple test endpoint first
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(ApiResponse.success("Auth controller working", "Test successful"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                                                   HttpServletRequest request) {
        logger.info("Authentication attempt for user: {}", loginRequest.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        
        String jwt = jwtUtil.generateToken(userDetails);
        
        JwtResponse jwtResponse = new JwtResponse(jwt, user.getId(), user.getUsername(), 
                                                user.getEmail(), user.getRole().name());
        
        // Log security event
        String ipAddress = getClientIpAddress(request);
        auditService.logSecurityEvent(user.getUsername(), "LOGIN_SUCCESS", ipAddress);
        
        logger.info("User authenticated successfully: {}", user.getUsername());
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody RegisterRequest registerRequest,
                                                        HttpServletRequest request) {
        logger.info("Registration attempt for user: {}", registerRequest.getUsername());
        
        User user = userService.createUser(registerRequest);
        
        // Log security event
        String ipAddress = getClientIpAddress(request);
        auditService.logSecurityEvent(user.getUsername(), "REGISTRATION_SUCCESS", ipAddress);
        
        logger.info("User registered successfully: {}", user.getUsername());
        
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.isEmpty()) {
            return request.getRemoteAddr();
        } else {
            return xForwardedFor.split(",")[0].trim();
        }
    }
}