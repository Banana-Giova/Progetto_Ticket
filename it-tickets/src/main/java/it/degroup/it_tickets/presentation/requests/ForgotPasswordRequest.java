package it.degroup.it_tickets.presentation.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @Email
    @NotBlank(message = "User email cannot be blank")
    private String userEmail;
}
