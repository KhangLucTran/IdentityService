package com.example.SpringNe.validator;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import com.example.SpringNe.validator.DobConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    private int min;

    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min  = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        if(Objects.isNull(dob))
            return true;
        long years = ChronoUnit.YEARS.between(dob, LocalDate.now());
        return years >= min;
    }
}