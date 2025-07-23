package it.degroup.it_tickets.presentation.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailTokenRequest {
    @NotBlank(message = "Email cannot be blank")
    private String token;
}
