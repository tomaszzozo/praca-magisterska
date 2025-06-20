package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class NameValidatorUnitTests {
	@Mock
	private ConstraintValidatorContext ctx;

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.InvalidDataProvider#names")
	void isValidReturnsFalseForInvalidNames(String invalidName) {
		Assertions.assertFalse(new NameValidator().isValid(invalidName, ctx));
	}

	@ParameterizedTest
	@MethodSource("com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers.ValidDataProvider#names")
	void isValidReturnsTrueForValidNames(String validName) {
		Assertions.assertTrue(new NameValidator().isValid(validName, ctx));
	}

	@Test
	void isValidReturnsTrueForNull() {
		Assertions.assertTrue(new NameValidator().isValid(null, ctx));
	}
}
