package it.degroup.it_tickets.presentation.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class LoginRequest {

    @Email(message = "invalid email")
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotBlank(message = "password cannot be blank")
    private String password;

}
