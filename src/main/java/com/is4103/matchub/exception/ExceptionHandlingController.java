/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.exception;

import java.io.IOException;
import java.util.ArrayList;
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
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(UserNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("User not found");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(value = MessagingException.class)
    public ResponseEntity<ExceptionResponse> sendEmailFailure(MessagingException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Failure sending email: MessagingException");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<ExceptionResponse> sendEmailFailure(IOException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Failure sending email: IOException");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = {InvalidRequestException.class})
    public ResponseEntity<ExceptionResponse> invalidFileExtension(InvalidRequestException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Failed to upload file: Invalid File Extension");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DeleteProfilePictureException.class)
    public ResponseEntity<ExceptionResponse> unableToDeleteProfilePicture(DeleteProfilePictureException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to delete profile picture");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UpdateProfileException.class)
    public ResponseEntity<ExceptionResponse> unableToUpdateProfile(UpdateProfileException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to Update Profile");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnableToFollowProfileException.class)
    public ResponseEntity<ExceptionResponse> unableToFollowProfile(UnableToFollowProfileException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to Follow Profile");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnableToUnfollowProfileException.class)
    public ResponseEntity<ExceptionResponse> unableToUnfollowProfile(UnableToUnfollowProfileException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to Unfollow Profile");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnableToRemoveFollowerException.class)
    public ResponseEntity<ExceptionResponse> unableToRemoveFollower(UnableToRemoveFollowerException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to Remove Follower");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = ProjectNotFoundException.class)
    public ResponseEntity<ExceptionResponse> ProjectNotFound(ProjectNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Project Not Found");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    
            
    @ExceptionHandler(value = UpdateProjectException.class)
    public ResponseEntity<ExceptionResponse> UnableToUpdateProject(UpdateProjectException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to update project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = DeleteProjectException.class)
    public ResponseEntity<ExceptionResponse> UnableToDeleteProject(DeleteProjectException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to delete project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = TerminateProjectException.class)
    public ResponseEntity<ExceptionResponse> TerminateProject(TerminateProjectException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to terminate project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
}
