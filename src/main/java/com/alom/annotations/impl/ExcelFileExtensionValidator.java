package com.alom.annotations.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import com.alom.annotations.ValidExcelFileExtension;

import java.util.Arrays;
import java.util.List;

public class ExcelFileExtensionValidator implements ConstraintValidator<ValidExcelFileExtension, MultipartFile> {

    private List<String> acceptedExtensions;

    @Override
    public void initialize(ValidExcelFileExtension constraintAnnotation) {
        // Convert the accepted extensions from the annotation to a List
        acceptedExtensions = Arrays.asList(constraintAnnotation.acceptedExtensions());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // If the file is null, return true (can be handled separately)
        if (file == null || file.isEmpty()) {
            return true; 
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
            return acceptedExtensions.contains(fileExtension);
        }
        return false; // Invalid if no extension found
    }
}
