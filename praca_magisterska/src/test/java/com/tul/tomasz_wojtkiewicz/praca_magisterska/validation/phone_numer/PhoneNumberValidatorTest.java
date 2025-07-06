package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer;// Testy jednostkowe dla PhoneNumberValidator

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Tag("unit")
// ai tag: unit
class PhoneNumberValidatorTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.phone_numer'

	private PhoneNumberValidator validator;
	private ConstraintValidatorContext context;

	@BeforeEach
	void setUp() {
		validator = new PhoneNumberValidator();
		context = Mockito.mock(ConstraintValidatorContext.class);
	}

	@Test
		// cases: 1
	void shouldReturnTrueForNull() {
		assertTrue(validator.isValid(null, context));
	}

	@Test
		// cases: 1
	void shouldReturnTrueForValidPhoneNumber() {
		assertTrue(validator.isValid("123456789", context));
	}

	@Test
		// cases: 1
	void shouldReturnFalseForTooShort() {
		assertFalse(validator.isValid("12345678", context));
	}

	@Test
		// cases: 1
	void shouldReturnFalseForTooLong() {
		assertFalse(validator.isValid("1234567890", context));
	}

	@Test
		// cases: 1
	void shouldReturnFalseForNonDigits() {
		assertFalse(validator.isValid("12345a789", context));
	}

	@Test
		// cases: 1
	void shouldReturnFalseForEmptyString() {
		assertFalse(validator.isValid("", context));
	}
}
