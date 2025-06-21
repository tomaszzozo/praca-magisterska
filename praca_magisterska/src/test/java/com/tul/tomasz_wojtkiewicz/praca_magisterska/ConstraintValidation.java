package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

public interface ConstraintValidation {
	Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	default <T> Set<ConstraintViolation<T>> validateConstraints(T obj) {
		return validator.validate(obj);
	}
}
