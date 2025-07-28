package it.degroup.it_tickets.common.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, PasswordConfirmation> {

    @Override
    public boolean isValid(PasswordConfirmation req, ConstraintValidatorContext context) {
        if (req.getNewPassword() == null || req.getConfirmPassword() == null) {
            return false;
        }
        return req.getNewPassword().equals(req.getConfirmPassword());
    }
}
