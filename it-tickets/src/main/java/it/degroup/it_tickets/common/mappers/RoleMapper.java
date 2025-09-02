package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.presentation.responses.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "users", source = "users")
    RoleResponse roleToRoleResponse(Role role);
}
