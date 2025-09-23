package it.degroup.it_tickets.service.Role;

import it.degroup.it_tickets.presentation.responses.RoleResponseWOUsers;
import it.degroup.it_tickets.presentation.responses.UserResponseWithRoles;
import it.degroup.it_tickets.presentation.responses.RoleResponseWithUsers;

import java.util.List;

public interface RoleService {
    RoleResponseWithUsers createRole(String roleName);
    RoleResponseWithUsers getRole(String roleName);
    List<RoleResponseWOUsers> getAllRoles();
    List<UserResponseWithRoles> getUsersByRole(String roleName);
    void assignRoleToUser(String email, String roleName);
    void removeRoleFromUser(String email, String roleName);
}
