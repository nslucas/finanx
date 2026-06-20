package com.example.finanx.Exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {
    @ExceptionHandler(ObjectNotFoundException.class)

    public ResponseEntity<StandardError> objectNotFound(ObjectNotFoundException e, HttpServletRequest request){
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(System.currentTimeMillis(), httpStatus.value(), "Not found", e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> badRequest(IllegalArgumentException e, HttpServletRequest request){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(System.currentTimeMillis(), httpStatus.value(), "Bad request", e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> malformedJson(HttpMessageNotReadableException e, HttpServletRequest request){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(System.currentTimeMillis(), httpStatus.value(), "Bad request",
                "Request body is not valid JSON. Send an object like {\"email\":\"user@example.com\",\"password\":\"secret\"}.",
                request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(err);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> accessDenied(AccessDeniedException e, HttpServletRequest request){
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(System.currentTimeMillis(), httpStatus.value(), "Forbidden", e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(err);
    }

}
