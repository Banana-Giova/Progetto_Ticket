package it.degroup.it_tickets.presentation.responses;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponseWithRoles {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private Boolean emailConfirmed;
    private Set<RoleResponseWOUsers> roles;
    private String highestRole;
}
