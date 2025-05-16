package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_number;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer.PhoneNumberValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PhoneNumberValidatorUnitTests {
    @Mock
    private ConstraintValidatorContext ctx;

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#phoneNumbers")
    void given_invalidPhoneNumber_when_isValidIsCalled_then_itReturnsFalse(String invalidPhoneNumber) {
        assertFalse(new PhoneNumberValidator().isValid(invalidPhoneNumber, ctx));
    }

    @ParameterizedTest
    @MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#phoneNumbers")
    void given_validPhoneNumber_when_isValidIsCalled_then_itReturnsTrue(String validPhoneNumber) {
        assertTrue(new PhoneNumberValidator().isValid(validPhoneNumber, ctx));
    }

    @Test
    void given_null_when_isValidIsCalled_then_itReturnsTrue() {
        assertTrue(new PhoneNumberValidator().isValid(null, ctx));
    }
}
