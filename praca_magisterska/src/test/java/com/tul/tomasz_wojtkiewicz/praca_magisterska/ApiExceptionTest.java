package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

// INACCURACY: class should be package-private
@Tag("unit")
// ai tag: unit
public class ApiExceptionTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska'

	@Test
		// CASES: 1
	void testConstructorAndGetters() {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = "Test error message";

		ApiException exception = new ApiException(status, message);

		assertEquals(status, exception.getStatus());
		assertEquals(message, exception.getLoggerMessage());
		assertEquals(message, exception.getMessage());
	}
}
