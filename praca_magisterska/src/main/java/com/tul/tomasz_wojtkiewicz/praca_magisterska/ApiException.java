package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String loggerMessage;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.loggerMessage = message;
    }
}
