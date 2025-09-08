package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.RoleResponseWithUsers;
import it.degroup.it_tickets.presentation.responses.RoleResponseWOUsers;
import it.degroup.it_tickets.presentation.responses.UserResponseWORoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    // user list viene mappata tramite il metodo di supporto mapUsersToUsers
    @Mapping(target = "users", source = "users")
    RoleResponseWithUsers roleToRoleResponseWithUsers(Role role);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleResponseWOUsers roleToRoleResponseWOUsers(Role role);

    // default helper: mappa Set<User> -> List<UserResponseWORoles> direttamente
    default Set<UserResponseWORoles> mapUsersToUsers(Set<User> users) {
        if (users == null) return null;
        Set<UserResponseWORoles> set = new HashSet<>();
        for (User u : users) {
            UserResponseWORoles r = new UserResponseWORoles();
            r.setId(u.getId());
            r.setEmail(u.getEmail());
            r.setName(u.getName());
            r.setSurname(u.getSurname());
            set.add(r);
        }
        return set;
    }
}
