package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
// ai tag: unit
class PhoneNumberValidatorTest {

	private final PhoneNumberValidator validator = new PhoneNumberValidator();

	@Test
	void shouldReturnTrueForNull() {
		assertTrue(validator.isValid(null, null));
	}

	@Test
	void shouldReturnTrueForValidPhoneNumber() {
		assertTrue(validator.isValid("123456789", null));
	}

	@Test
	void shouldReturnFalseForPhoneNumberWithLetters() {
		assertFalse(validator.isValid("12345a789", null));
	}

	@Test
	void shouldReturnFalseForTooShortPhoneNumber() {
		assertFalse(validator.isValid("12345678", null));
	}

	@Test
	void shouldReturnFalseForTooLongPhoneNumber() {
		assertFalse(validator.isValid("1234567890", null));
	}

	@Test
	void shouldReturnFalseForPhoneNumberWithSpaces() {
		assertFalse(validator.isValid("123 456 789", null));
	}
}
