package com.is4103.matchub.exception;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

public class ValidationUtil {

    public static List<String> fromBindingErrors(Errors errors) {
        List<String> validErrors = new ArrayList<String>();
        for (ObjectError objectError : errors.getAllErrors()) {
            validErrors.add(objectError.getDefaultMessage());
        }
        return validErrors;
    }

    public static List<String> fromError(String errorMessage) {
        List<String> validErrors = new ArrayList<String>();

        validErrors.add(errorMessage);
        return validErrors;
    }

}
