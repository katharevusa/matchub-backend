package com.is4103.matchub.validation;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StringArrayEnumValidator implements ConstraintValidator<StringArrayEnum, String[]> {

    private Set<String> validValues;

    @Override
    public void initialize(StringArrayEnum constraintAnnotation) {
        validValues = Arrays.stream(constraintAnnotation.values()).collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String[] strArray, ConstraintValidatorContext context) {
        if (strArray == null) {
            return true;
        }
        for (int i = 0; i < strArray.length; i++) {
            if (!validValues.contains(strArray[i])) {
                return false;
            }
        }
        return true;
    }
}
