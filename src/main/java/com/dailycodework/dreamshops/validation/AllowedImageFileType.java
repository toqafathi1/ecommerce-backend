package com.dailycodework.dreamshops.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FileTypeValidator.class )
@Target({ElementType.FIELD , ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedImageFileType {
    String message() default "Invalid file type . Allowed types are: image/jpeg, image/jpg , image/png";
    Class<?>[] groups() default{};
    Class<? extends Payload> [] payload() default {};

}
