package it.degroup.it_tickets.presentation.requests;

import it.degroup.it_tickets.common.validation.password.PasswordConfirmation;
import it.degroup.it_tickets.common.validation.password.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AssignRoleRequest {
    @Email
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @NotBlank(message = "Role cannot be blank")
    private String roleName;
}
