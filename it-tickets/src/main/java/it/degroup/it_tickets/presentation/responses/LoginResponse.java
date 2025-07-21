package it.degroup.it_tickets.presentation.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class LoginResponse {

    private String token;
}
