package it.degroup.it_tickets.service.Role;

import it.degroup.it_tickets.presentation.responses.ProfileResponse;
import it.degroup.it_tickets.presentation.responses.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(String roleName);
    RoleResponse getRole(String roleName);
    List<ProfileResponse> getUsersByRole(String roleName);
    void assignRoleToUser(String email, String roleName);
}
