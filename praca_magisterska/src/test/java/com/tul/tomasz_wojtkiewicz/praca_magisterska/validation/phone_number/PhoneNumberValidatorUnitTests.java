package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_number;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer.PhoneNumberValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PhoneNumberValidatorUnitTests {
    @Mock
    private ConstraintValidatorContext ctx;

    @Test
    void given_invalidPhoneNumber_when_isValidIsCalled_then_itReturnsFalse() {
        List.of("12345678", "1234567890", "123456f89", "123 45678", "").forEach(phoneNumber -> Assertions.assertFalse(new PhoneNumberValidator().isValid(phoneNumber, ctx), phoneNumber));
    }

    @Test
    void given_validPhoneNumber_when_isValidIsCalled_then_itReturnsTrue() {
        List.of("123456789", "987654321", "000000000", "555555555", "123123123").forEach(phoneNumber -> Assertions.assertTrue(new PhoneNumberValidator().isValid(phoneNumber, ctx), phoneNumber));
    }

    @Test
    void given_null_when_isValidIsCalled_then_itReturnsTrue() {
        Assertions.assertTrue(new PhoneNumberValidator().isValid(null, ctx));
    }
}
