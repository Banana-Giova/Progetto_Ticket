package it.degroup.it_tickets.presentation.requests;

import it.degroup.it_tickets.common.validation.password.PasswordConfirmation;
import it.degroup.it_tickets.common.validation.password.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@PasswordMatches
public class ResetPasswordRequest implements PasswordConfirmation {

    private String userEmail;

    private String passwordToken;

    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,24}$",
            message = "The password must contain at least 8 characters, an uppercase letter, a lowercase letter, a number and a special character"
    )
    private String newPassword;

    @NotBlank(message = "Confirm password cannot be blank")
    private String confirmPassword;
}
