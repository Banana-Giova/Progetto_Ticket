package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    RegisterResponse userToRegisterResponse(User user);
}
