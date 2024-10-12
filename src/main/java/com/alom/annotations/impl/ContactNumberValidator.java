package com.alom.annotations.impl;

import com.alom.annotations.ValidContactNumber;
import com.alom.constant.ApiResponseMessage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContactNumberValidator implements ConstraintValidator<ValidContactNumber, String> {

    @Override
    public void initialize(ValidContactNumber constraintAnnotation) {
        // Initialization logic, if necessary
    }

    @Override
    public boolean isValid(String contactNumber, ConstraintValidatorContext context) {
        // Check if the contact number is null or empty
        if (contactNumber == null || contactNumber.isEmpty()) {
            return false; // Or true, depending on whether you want to allow null values
        }
        return contactNumber.matches(ApiResponseMessage.CONTACT_NUMBER_PATTERN);
    }
}
