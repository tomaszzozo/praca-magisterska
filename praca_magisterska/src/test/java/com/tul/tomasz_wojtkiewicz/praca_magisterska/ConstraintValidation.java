package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;

import java.util.Set;

public interface ConstraintValidation {
    default <T> Set<ConstraintViolation<T>> validateConstraints(T entity) {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(entity);
    }
}
