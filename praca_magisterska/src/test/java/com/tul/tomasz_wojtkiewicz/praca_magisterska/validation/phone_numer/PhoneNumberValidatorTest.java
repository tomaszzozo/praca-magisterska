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
		// cases: 1
	void shouldReturnTrueForNull() {
		assertTrue(validator.isValid(null, null));
	}

	@Test
		// cases: 1
	void shouldReturnTrueForValidPhoneNumber() {
		assertTrue(validator.isValid("123456789", null));
	}

	@Test
		// cases: 1
	void shouldReturnFalseForPhoneNumberWithLetters() {
		assertFalse(validator.isValid("12345a789", null));
	}

	@Test
		// cases: 1
	void shouldReturnFalseForTooShortPhoneNumber() {
		assertFalse(validator.isValid("12345678", null));
	}

	@Test
		// cases: 1
	void shouldReturnFalseForTooLongPhoneNumber() {
		assertFalse(validator.isValid("1234567890", null));
	}

	@Test
		// cases: 1
	void shouldReturnFalseForPhoneNumberWithSpaces() {
		assertFalse(validator.isValid("123 456 789", null));
	}
}
