package com.library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ISBNValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ISBN {
    
    String message() default "Invalid ISBN format";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}