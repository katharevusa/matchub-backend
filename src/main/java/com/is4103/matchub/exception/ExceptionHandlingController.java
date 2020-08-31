package com.is4103.matchub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.is4103.matchub.exception.UsernameConflictException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlingController {

    @ExceptionHandler(UsernameConflictException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(UsernameConflictException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Username Conflict");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.CONFLICT);
    }
}