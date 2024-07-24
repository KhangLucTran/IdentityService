package com.example.SpringNe.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.example.SpringNe.validator.DobValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DobValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface DobConstraint {
    int min();
    String message() default "Invalid date of birth";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}