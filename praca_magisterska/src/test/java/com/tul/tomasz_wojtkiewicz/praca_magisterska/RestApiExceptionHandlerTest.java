package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Tag("unit")
// ai tag: unit
class RestApiExceptionHandlerTest {

	private RestApiExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new RestApiExceptionHandler();
	}

	@Test
	void handleMethodArgumentNotValidException_returnsBadRequest() {
		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		ResponseEntity<String> response = handler.handleException(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Nieprawidłowy format danych"));
	}

	@Test
	void handleConstraintViolationException_returnsBadRequest() {
		ConstraintViolationException ex = mock(ConstraintViolationException.class);
		ResponseEntity<String> response = handler.handleException(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Nieprawidłowy format danych"));
	}

	@Test
	void handleUnexpectedTypeException_returnsBadRequest() {
		UnexpectedTypeException ex = mock(UnexpectedTypeException.class);
		ResponseEntity<String> response = handler.handleException(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Nieprawidłowy format danych"));
	}

	@Test
	void handleDataIntegrityViolationException_returnsBadRequest() {
		DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
		ResponseEntity<String> response = handler.handleException(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertTrue(response.getBody().contains("Nieprawidłowy format danych"));
	}

	@Test
	void handleApiException_returnsCustomStatusAndMessage() {
		ApiException ex = new ApiException(HttpStatus.NOT_FOUND, "Not found error");
		ResponseEntity<String> response = handler.handleException(ex);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Not found error", response.getBody());
	}
}
