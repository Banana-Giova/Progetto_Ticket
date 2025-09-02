package it.degroup.it_tickets.presentation.responses;

import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class RoleResponse {

    private Long id;
    private String name;
    private List<User> users;
}
