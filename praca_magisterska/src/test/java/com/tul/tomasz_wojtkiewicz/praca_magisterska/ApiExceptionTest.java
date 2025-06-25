package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
// ai tag: unit
class ApiExceptionTest {

	@Test
		// cases: 1
	void shouldStoreHttpStatusAndMessages() {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = "Invalid request";

		ApiException exception = new ApiException(status, message);

		assertEquals(status, exception.getStatus());
		assertEquals(message, exception.getLoggerMessage());
		assertEquals(message, exception.getMessage());
	}
}
