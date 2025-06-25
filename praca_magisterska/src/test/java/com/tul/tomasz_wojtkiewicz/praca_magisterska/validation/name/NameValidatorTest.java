// Test jednostkowy
package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Tag("unit")
// ai tag: unit
class NameValidatorTest {

	private NameValidator validator;
	private ConstraintValidatorContext context;

	@BeforeEach
	void setUp() {
		validator = new NameValidator();
		context = mock(ConstraintValidatorContext.class);
	}

	@Test
	void shouldAcceptValidNames() {
		assertTrue(validator.isValid("Jan Kowalski", context));
		assertTrue(validator.isValid("Łukasz Żółć", context));
		assertTrue(validator.isValid("Anna-Maria Nowak", context));
		assertTrue(validator.isValid("Jean-Luc Picard", context));
		assertTrue(validator.isValid("O'Connor", context));
		// MISTAKE: name not valid assertTrue(validator.isValid("Émile Zola", context));
	}

	@Test
	void shouldRejectInvalidNames() {
		assertFalse(validator.isValid("  Jan", context));
		assertFalse(validator.isValid("Anna--Maria", context));
		assertFalse(validator.isValid("Jan..Kowalski", context));
		assertFalse(validator.isValid("Nowak-", context));
		assertFalse(validator.isValid("Kowalski'", context));
		assertFalse(validator.isValid("Jan  Kowalski", context));
		assertFalse(validator.isValid("Jan__Kowalski", context));
	}

	@Test
	void shouldAcceptNullValue() {
		assertTrue(validator.isValid(null, context));
	}
}
