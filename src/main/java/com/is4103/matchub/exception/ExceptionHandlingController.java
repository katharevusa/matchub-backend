/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.is4103.matchub.exception;

import com.google.firebase.auth.FirebaseAuthException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
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
    public ResponseEntity<ExceptionResponse> userNotFoundException(UserNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("User not found");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = OrganisationNotFoundException.class)
    public ResponseEntity<ExceptionResponse> organisationNotFoundException(OrganisationNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Organisation not found");
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

    @ExceptionHandler(value = UploadOrganisationVerificationDocException.class)
    public ResponseEntity<ExceptionResponse> uploadOrgVerificationDocFailure(UploadOrganisationVerificationDocException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Failed to upload organisation verification file");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NoSuchFileException.class)
    public ResponseEntity<ExceptionResponse> unableToDeleteFile(NoSuchFileException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Failed to delete file");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DeleteOrganisationVerificationDocumentException.class)
    public ResponseEntity<ExceptionResponse> unableToDeleteOrganisationDoc(DeleteOrganisationVerificationDocumentException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Failed to delete organisation document");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<ExceptionResponse> projectNotFound(ProjectNotFoundException ex) {
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
    public ResponseEntity<ExceptionResponse> unableToDeleteProject(DeleteProjectException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to delete project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = TerminateProjectException.class)
    public ResponseEntity<ExceptionResponse> terminateProjectError(TerminateProjectException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to terminate project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceNotFound(ResourceNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Resource not found");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceCategoryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceCategoryNotFound(ResourceCategoryNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Resource category not found");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UpdateResourceException.class)
    public ResponseEntity<ExceptionResponse> updateResourceException(UpdateResourceException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to update resource");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DownvoteProjectException.class)
    public ResponseEntity<ExceptionResponse> downvoteProjectException(DownvoteProjectException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to downvote project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = TerminateResourceException.class)
    public ResponseEntity<ExceptionResponse> terminateResourceException(TerminateResourceException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to terminate resource");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UpvoteProjectException.class)
    public ResponseEntity<ExceptionResponse> upvoteProjectException(UpvoteProjectException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to upvote project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RevokeDownvoteException.class)
    public ResponseEntity<ExceptionResponse> revokeDownvoteException(RevokeDownvoteException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to revoke downvote for this project ");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RevokeUpvoteException.class)
    public ResponseEntity<ExceptionResponse> revokeUpvoteException(RevokeUpvoteException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to revoke upvote for this project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CreateResourceRequestException.class)
    public ResponseEntity<ExceptionResponse> createResourceRequestException(CreateResourceRequestException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to create resource request");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DeleteResourceRequestException.class)
    public ResponseEntity<ExceptionResponse> deleteResourceRequestException(DeleteResourceRequestException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to delete resource request");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceRequestNotFoundException.class)
    public ResponseEntity<ExceptionResponse> resourceRequestNotFoundException(ResourceRequestNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to find resource request");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = FirebaseAuthException.class)
    public ResponseEntity<ExceptionResponse> firebaseAuthException(FirebaseAuthException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to authenticate user on Firebase");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PostNotFoundException.class)
    public ResponseEntity<ExceptionResponse> postNotFoundException(PostNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Post Not Found.");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RespondToResourceRequestException.class)
    public ResponseEntity<ExceptionResponse> respondToResourceRequestException(RespondToResourceRequestException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to respond resource request");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnableToDeletePostException.class)
    public ResponseEntity<ExceptionResponse> postNotFoundException(UnableToDeletePostException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to delete post.");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UpdatePostException.class)
    public ResponseEntity<ExceptionResponse> updatePostException(UpdatePostException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to update post.");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnableToAddMemberToOrganisationException.class)
    public ResponseEntity<ExceptionResponse> unableToAddMemberToOrganisationException(UnableToAddMemberToOrganisationException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable add member into organisation.");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnableToRemoveMemberFromOrganisationException.class)
    public ResponseEntity<ExceptionResponse> unableToRemoveMemberFromOrganisationException(UnableToRemoveMemberFromOrganisationException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to remove member from organisation.");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnableToAddKAHToOrganisationException.class)
    public ResponseEntity<ExceptionResponse> unableToAddKAHTOOrganisationException(UnableToAddKAHToOrganisationException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to add KAH into organisation.");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = UnableToRemoveKAHFromOrganisationException.class)
    public ResponseEntity<ExceptionResponse> unableToRemoveKAHFromOrganisationException(UnableToRemoveKAHFromOrganisationException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to remove KAH from organisation.");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = RespondToJoinProjectRequestException.class)
    public ResponseEntity<ExceptionResponse> respondToJoinProjectRequestException(RespondToJoinProjectRequestException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to respond to the join project request");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = RemoveTeamMemberException.class)
    public ResponseEntity<ExceptionResponse> removeTeamMemberException(RemoveTeamMemberException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to remove team member");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(value = LeaveProjectException.class)
    public ResponseEntity<ExceptionResponse> leaveProjectException(LeaveProjectException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Unable to leave project");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(ValidationUtil.fromError(ex.getMessage()));

        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }
    


}
