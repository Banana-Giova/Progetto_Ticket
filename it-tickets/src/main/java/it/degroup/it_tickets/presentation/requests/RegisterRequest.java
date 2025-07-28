package it.degroup.it_tickets.presentation.requests;

import it.degroup.it_tickets.common.validation.password.PasswordConfirmation;
import it.degroup.it_tickets.common.validation.password.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@PasswordMatches
public class RegisterRequest implements PasswordConfirmation {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Surname cannot be blank")
    private String surname;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,24}$",
            message = "The password must contain at least 8 characters, an uppercase letter, a lowercase letter, a number and a special character"
    )
    private String newPassword;

    @NotBlank(message = "Confirm password cannot be blank")
    private String confirmPassword;
}
