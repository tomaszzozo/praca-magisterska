package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements
        ConstraintValidator<PhoneNumber, String> {
    @Override
    public boolean isValid(String field,
                           ConstraintValidatorContext cxt) {
        return field == null || field.matches("^\\d{9}$");
    }
}
