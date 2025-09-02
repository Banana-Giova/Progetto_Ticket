package it.degroup.it_tickets.presentation.responses;

import it.degroup.it_tickets.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class ProfileResponse {

    private Long id;
    private String email;
    private String name;
    private String surname;
    private Set<Role> roles;
}
