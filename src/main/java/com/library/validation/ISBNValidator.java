package com.library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ISBNValidator implements ConstraintValidator<ISBN, String> {
    
    private static final Pattern ISBN_PATTERN = Pattern.compile(
        "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$"
    );
    
    @Override
    public void initialize(ISBN constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        
        // Remove all non-digit characters for validation
        String digitsOnly = isbn.replaceAll("\\D", "");
        
        // Check length (10 or 13 digits)
        if (digitsOnly.length() != 10 && digitsOnly.length() != 13) {
            return false;
        }
        
        // Validate checksum for ISBN-10
        if (digitsOnly.length() == 10) {
            return isValidISBN10(digitsOnly);
        }
        
        // Validate checksum for ISBN-13
        return isValidISBN13(digitsOnly);
    }
    
    private boolean isValidISBN10(String isbn) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (10 - i) * digit;
        }
        
        char checkDigit = isbn.charAt(9);
        if (checkDigit == 'X') {
            sum += 10;
        } else {
            sum += Character.getNumericValue(checkDigit);
        }
        
        return sum % 11 == 0;
    }
    
    private boolean isValidISBN13(String isbn) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        int checkDigit = Character.getNumericValue(isbn.charAt(12));
        int calculatedCheckDigit = (10 - (sum % 10)) % 10;
        
        return checkDigit == calculatedCheckDigit;
    }
}
