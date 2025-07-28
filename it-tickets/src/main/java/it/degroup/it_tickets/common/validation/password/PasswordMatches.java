package it.degroup.it_tickets.common.validation.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default "Password e conferma password non coincidono.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
