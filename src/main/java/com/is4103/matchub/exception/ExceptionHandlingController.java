/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.exception;

import java.io.IOException;
import javax.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;

/**
 *
 * @author ngjin
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlingController {

    @ExceptionHandler(value = EmailExistException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(EmailExistException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Email exists");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> invalidInput(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Validation Error");
        response.setErrorMessage("Invalid inputs.");
        response.setErrors(ValidationUtil.fromBindingErrors(result));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MessagingException.class, IOException.class})
    public ResponseEntity<ExceptionResponse> sendEmailFailure(MessagingException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Failure sending email");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = {InvalidRequestException.class})
    public ResponseEntity<ExceptionResponse> invalidFileExtension(InvalidRequestException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Failed to upload file: Invalid File Extension");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DeleteProfilePictureException.class)
    public ResponseEntity<ExceptionResponse> unableToDeleteProfilePicture(DeleteProfilePictureException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to delete profile picture");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = UpdateProfileException.class)
    public ResponseEntity<ExceptionResponse> unableToUpdateProfile(UpdateProfileException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to Update Profile");
        response.setErrorMessage(ex.getMessage());

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
}
