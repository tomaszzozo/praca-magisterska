package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_number;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer.PhoneNumberValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PhoneNumberValidatorUnitTests {
	@Mock
	private ConstraintValidatorContext ctx;

	private static Stream<Arguments> invalidPhoneNumbers() {
		return Stream.of("", " ", "1", "22", "333", "4444", "55555", "666666", "7777777", "88888888", "9999999999", "99999999999", "asd", "12345678i", "12g345678", "q12345678i", "123-456-789", "123456-78").map(Arguments::of);
	}

	private static Stream<Arguments> validPhoneNumbers() {
		return Stream.of("123456789", "987654321", "000000000", "555555555", "123123123").map(Arguments::of);
	}

	@ParameterizedTest
	@MethodSource("invalidPhoneNumbers")
	void given_invalidPhoneNumber_when_isValidIsCalled_then_itReturnsFalse(String invalidPhoneNumber) {
		assertFalse(new PhoneNumberValidator().isValid(invalidPhoneNumber, ctx));
	}

	@ParameterizedTest
	@MethodSource("validPhoneNumbers")
	void given_validPhoneNumber_when_isValidIsCalled_then_itReturnsTrue(String validPhoneNumber) {
		assertTrue(new PhoneNumberValidator().isValid(validPhoneNumber, ctx));
	}

	@Test
	void given_null_when_isValidIsCalled_then_itReturnsTrue() {
		assertTrue(new PhoneNumberValidator().isValid(null, ctx));
	}
}
