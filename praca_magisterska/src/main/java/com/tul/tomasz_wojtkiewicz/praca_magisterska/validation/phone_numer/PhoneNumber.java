package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PhoneNumberValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "{com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_number.PhoneNumber.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
