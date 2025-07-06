// Testy jednostkowe dla klasy NameValidator

package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Tag("unit")
// ai tag: unit
// MISTAKE: two classes were generated with the same name for two different source files
// INACCURACY: similar tests in two different files
class NameValidatorUnitTest2 {

	private NameValidator validator;

	@BeforeEach
	void setUp() {
		validator = new NameValidator();
	}

	@Test
		// CASES: 1
	void isValid_ShouldReturnTrue_WhenNull() {
		assertTrue(validator.isValid(null, mock(ConstraintValidatorContext.class)));
	}

	@Test
		// CASES: 7
	void isValid_ShouldReturnTrue_ForValidNames() {
		assertTrue(validator.isValid("Jan", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("Anna-Maria", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("O'Connor", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("Jean-Luc", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("Łukasz", mock(ConstraintValidatorContext.class)));
		// MISTAKE: this name should not pass validation assertTrue(validator.isValid("Maria .", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("A.B.", mock(ConstraintValidatorContext.class)));
		assertTrue(validator.isValid("A B", mock(ConstraintValidatorContext.class)));
	}
	@Test
		// CASES: 10
	void isValid_ShouldReturnFalse_ForInvalidNames() {
		assertFalse(validator.isValid("", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Jan123", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Anna@", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Name!", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("John__Doe", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("  ", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Anna--Maria", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Anna..Maria", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Anna,,Maria", mock(ConstraintValidatorContext.class)));
		assertFalse(validator.isValid("Anna  Maria", mock(ConstraintValidatorContext.class)));
	}
}
