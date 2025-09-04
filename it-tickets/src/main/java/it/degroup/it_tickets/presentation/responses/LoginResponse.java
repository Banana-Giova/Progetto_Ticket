package it.degroup.it_tickets.presentation.responses;

import it.degroup.it_tickets.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor @AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long id;
    private String email;
    private String name;
    private String surname;
    private List<String> roles;
}
