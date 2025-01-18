package com.dailycodework.dreamshops.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class FileTypeValidator implements ConstraintValidator<AllowedImageFileType,String> {
    private static final Set<String> ALLOWED_FILE_TYPES =Set.of("image/jpeg", "image/jpg", "image/png") ;
    @Override
    public boolean isValid(String fileType, ConstraintValidatorContext context) {
        if(fileType ==null || fileType.isEmpty()){
            return false;
        }
        return ALLOWED_FILE_TYPES.contains(fileType.toLowerCase());
    }
}
