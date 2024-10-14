package com.event.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ApplicationException extends ResponseStatusException {

    private String code;
    private String message;

    public ApplicationException(HttpStatusCode status) {
        super(status);
    }

    public ApplicationException(HttpStatusCode status, String code) {
        super(status, code);
    }

    public ApplicationException(HttpStatusCode status, String code, String message) {
        super(status, message);
        this.code = code;
        this.message = message;
    }

    public ApplicationException(HttpStatusCode status, String code, String message, Throwable cause) {
        super(status, message, cause);
        this.code = code;
        this.message = message;
    }

    public ApplicationException(HttpStatusCode status, String code, Throwable cause) {
        super(status, code, cause);
    }

}
