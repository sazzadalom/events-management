package com.alom.annotations.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import com.alom.annotations.ValidFileExtension;

import java.util.Arrays;
import java.util.List;

public class ExcelFileExtensionValidator implements ConstraintValidator<ValidFileExtension, MultipartFile> {

    private List<String> acceptedExtensions;

    @Override
    public void initialize(ValidFileExtension constraintAnnotation) {
        // Convert the accepted extensions from the annotation to a List
        acceptedExtensions = Arrays.asList(constraintAnnotation.acceptedExtensions());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Allow empty file validation, can customize this if required
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
            if (!acceptedExtensions.contains(fileExtension)) {
                // Disable default constraint violation message
                context.disableDefaultConstraintViolation();

                // Get the user-defined message from the annotation
                String customMessage = context.getDefaultConstraintMessageTemplate();

                // Use a default message if no custom message is provided
                String errorMessage = (customMessage != null && !customMessage.isEmpty()) 
                                      ? customMessage 
                                      : "Invalid file extension. Accepted extensions: " + String.join(", ", acceptedExtensions);

                // Build the constraint violation with the custom message
                context.buildConstraintViolationWithTemplate(errorMessage)
                       .addConstraintViolation();
                return false;
            }
            return true;
        }
        return false; // No extension found, return as invalid
    }


}
