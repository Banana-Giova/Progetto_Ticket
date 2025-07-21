package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "email", source = "entity.email")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "surname", source = "entity.surname")
    RegisterResponse userToRegisterResponse(User user);
}
