package com.example.user_service.validators.image;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class ImagefileValidator implements ConstraintValidator<ImageValidator, MultipartFile> {


    @Override
    public void initialize(ImageValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile.isEmpty()){
            return false;
        }
        String fileType = multipartFile.getContentType();
        if (!(fileType.equals("image/jpg") ||
                fileType.equals("image/png") ||
                fileType.equals("image/jpeg"))) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Only png and jpg is allowed").addConstraintViolation();
            return false;
        }

        return true;
    }
}
