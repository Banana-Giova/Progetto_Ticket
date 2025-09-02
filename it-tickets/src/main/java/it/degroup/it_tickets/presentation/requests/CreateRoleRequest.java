package it.degroup.it_tickets.presentation.requests;

import it.degroup.it_tickets.common.validation.password.PasswordConfirmation;
import it.degroup.it_tickets.common.validation.password.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@PasswordMatches
public class CreateRoleRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
}
