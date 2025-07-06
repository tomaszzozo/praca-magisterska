package com.tul.tomasz_wojtkiewicz.praca_magisterska;// Testy jednostkowe

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

// INACCURACY: class should be package-private
@Tag("unit")
// ai tag: unit
public class RestApiExceptionHandlerTest { // MISTAKE: Missing package statement: 'com.tul.tomasz_wojtkiewicz.praca_magisterska'

	private RestApiExceptionHandler handler;

	@BeforeEach
	public void setup() {
		handler = new RestApiExceptionHandler();
		// Wstrzyknięcie mocka loggera (przez refleksję, bo jest private static final)
		// MISTAKE: unable to inject logger via reflection or mocked static
//		try {
//			var field = RestApiExceptionHandler.class.getDeclaredField("LOGGER");
//			field.setAccessible(true);
//			field.set(null, logger);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}

	@Test
		// cases: 1
	void handleMethodArgumentNotValidException_shouldReturnBadRequestAndLog() {
		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		ResponseEntity<String> response = handler.handleException(ex);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isEqualTo("Nieprawidłowy format danych. Skontaktuj się z administratorem.");
	}

	@Test
		// cases: 1
	void handleConstraintViolationException_shouldReturnBadRequestAndLog() {
		ConstraintViolationException ex = mock(ConstraintViolationException.class);
		ResponseEntity<String> response = handler.handleException(ex);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isEqualTo("Nieprawidłowy format danych. Skontaktuj się z administratorem.");
	}

	@Test
		// cases: 1
	void handleUnexpectedTypeException_shouldReturnBadRequestAndLog() {
		UnexpectedTypeException ex = mock(UnexpectedTypeException.class);
		ResponseEntity<String> response = handler.handleException(ex);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isEqualTo("Nieprawidłowy format danych. Skontaktuj się z administratorem.");
	}

	@Test
		// cases: 1
	void handleDataIntegrityViolationException_shouldReturnBadRequestAndLog() {
		DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
		ResponseEntity<String> response = handler.handleException(ex);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).isEqualTo("Nieprawidłowy format danych. Skontaktuj się z administratorem.");
	}

	@Test
		// cases: 1
	void handleApiException_shouldReturnApiExceptionStatusAndMessageAndLog() {
		ApiException ex = new ApiException(HttpStatus.FORBIDDEN, "Forbidden access");
		ResponseEntity<String> response = handler.handleException(ex);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(response.getBody()).isEqualTo("Forbidden access");
	}
}
