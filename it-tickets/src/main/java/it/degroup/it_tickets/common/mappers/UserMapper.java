package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.UserResponseWORoles;
import it.degroup.it_tickets.presentation.responses.UserResponseWithRoles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    @Mapping(target = "roles", source = "roles")
    UserResponseWithRoles userToUserResponseWithRoles(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    UserResponseWORoles userToUserResponseWORoles(User user);
}
