package com.tul.tomasz_wojtkiewicz.praca_magisterska.validation.name;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class NameValidatorUnitTests {
	@Mock
	private ConstraintValidatorContext ctx;

	private static Stream<Arguments> invalidNames() {
		return Stream.of("Al", "Jo", "A", "Jan123", "M@rek", "Kowalski_", "Anna-Maria!", "Łukasz#", "Zażółć@gęślą", "Иван", "张伟", "अर्जुन", "-Anna", ".Jan", "'Kowalski", "Anna-", "Kowalski'", "J@n K0w@lski", "M@r!a", "Zażółć@gęślą", "", " ").map(Arguments::of);
	}

	private static Stream<Arguments> validNames() {
		return Stream.of("Jan", "Kowalski", "Nowak-Jackson", "Dąbrowski", "Zofia", "O'Connel", "von der Osten", "Wiśniewska", "Anna Maria", "jr.", "dr.", "Łucja", "Żółć").map(Arguments::of);
	}

	@ParameterizedTest
	@MethodSource("invalidNames")
	void isValidReturnsFalseForInvalidNames(String invalidName) {
		Assertions.assertFalse(new NameValidator().isValid(invalidName, ctx));
	}

	@ParameterizedTest
	@MethodSource("validNames")
	void isValidReturnsTrueForValidNames(String validName) {
		Assertions.assertTrue(new NameValidator().isValid(validName, ctx));
	}

	@Test
	void isValidReturnsTrueForNull() {
		Assertions.assertTrue(new NameValidator().isValid(null, ctx));
	}
}
