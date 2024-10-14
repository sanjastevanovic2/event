package com.event.config;

import com.event.exception.ApiResponseExceptionMessage;
import com.event.exception.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdviser extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    ResponseEntity<ApiResponseExceptionMessage> handleStatusException(ApplicationException ex, WebRequest request) {
        return new ResponseEntity<>(ApiResponseExceptionMessage.builder()
                .code(ex.getCode())
                .path(request.getDescription(false).substring(4))
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiResponseExceptionMessage> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        return new ResponseEntity<>(ApiResponseExceptionMessage.builder()
                .code(String.valueOf(ex.getStatusCode()))
                .path(request.getDescription(false).substring(4))
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseExceptionMessage> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ApiResponseExceptionMessage.builder()
                .code("500")
                .path(request.getDescription(false).substring(4))
                .message(String.format("Error occurred: %s ", ex.getMessage()))
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    ResponseEntity<ApiResponseExceptionMessage> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request) {
        return new ResponseEntity<>(ApiResponseExceptionMessage.builder()
                .code(String.valueOf(ex.getStatusCode()))
                .path(request.getDescription(false).substring(4))
                .message(ex.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

}
