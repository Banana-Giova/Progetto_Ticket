package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.UserResponseWORoles;
import it.degroup.it_tickets.presentation.responses.UserResponseWithRoles;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    @Mapping(target = "emailConfirmed", source = "emailConfirmed")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "highestRole", ignore = true)
    UserResponseWithRoles userToUserResponseWithRoles(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    UserResponseWORoles userToUserResponseWORoles(User user);

    @AfterMapping
    default void setHighestRole(User user, @MappingTarget UserResponseWithRoles dto) {
        Set<Role> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            dto.setHighestRole(null);
        } else if (user.isAdmin()) {
            dto.setHighestRole("Amministratore");
        } else if (user.isOperator()) {
            dto.setHighestRole("Operatore");
        } else {
            dto.setHighestRole("Utente");
        }
    }
}
