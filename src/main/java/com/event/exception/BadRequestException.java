package com.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class BadRequestException extends ApplicationException{

    private static final HttpStatusCode status = HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value());

    public BadRequestException() {
        super(status);
    }

    public BadRequestException(String code, String message) {
        super(status, code, message);
    }

    public BadRequestException(String code, String message, Throwable cause) {
        super(status, code, message, cause);
    }

    public BadRequestException(String code, Throwable cause) {
        super(status, code, cause);
    }

}
