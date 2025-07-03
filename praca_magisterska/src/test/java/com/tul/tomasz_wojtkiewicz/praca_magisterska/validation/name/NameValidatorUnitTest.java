// Testy jednostkowe dla adnotacji walidacyjnej @Name i NameValidator

package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Tag("unit")
// ai tag: unit
class NameValidatorUnitTest {

	private NameValidator validator;

	@BeforeEach
	void setUp() {
		validator = new NameValidator();
		validator.initialize(null);
	}

	@Test
	void isValid_ShouldReturnFalse_WhenNull() {
		assertFalse(validator.isValid(null, mock(ConstraintValidatorContext.class)));
	}

	@Test
	void isValid_ShouldReturnFalse_WhenEmpty() {
		assertFalse(validator.isValid("", mock(ConstraintValidatorContext.class)));
	}

	@Test
	void isValid_ShouldReturnTrue_ForValidNames() {
		assertTrue(validator.isValid("Jan", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("Anna-Maria", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("O'Connor", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("Jean-Luc", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("Łukasz", mock(ConstraintValidatorContext.class)));
	}

	@Test
	void isValid_ShouldReturnFalse_ForInvalidNames() {
		assertFalse(validator.isValid("Jan123", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Anna@", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Name!", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("John_Doe", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid(" ", mock(ConstraintValidatorContext.class)));
	}
}
