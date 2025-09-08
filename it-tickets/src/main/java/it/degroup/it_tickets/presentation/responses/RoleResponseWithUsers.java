package it.degroup.it_tickets.presentation.responses;

import lombok.Data;
import java.util.Set;

@Data
public class RoleResponseWithUsers {
    private Long id;
    private String name;
    private Set<UserResponseWORoles> users;
}
