package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiExceptionHandler.class);

    private ResponseEntity<String> handleUnexpectedException(Throwable ex) {
        LOGGER.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nieprawidłowy format danych. Skontaktuj się z administratorem.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleException(MethodArgumentNotValidException ex) {
        return handleUnexpectedException(ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleException(ConstraintViolationException ex) {
        return handleUnexpectedException(ex);
   }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<String> handleException(UnexpectedTypeException ex) {
        return handleUnexpectedException(ex);
  }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleException(DataIntegrityViolationException ex) {
        return handleUnexpectedException(ex);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleException(ApiException ex) {
        LOGGER.error(ex.getLoggerMessage());
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}
