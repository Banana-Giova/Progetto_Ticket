package it.degroup.it_tickets.service.Role;

import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(String roleName);
    void assignRoleToUser(String email, String roleName);
    List<User> getUsersByRole(String roleName);
}
