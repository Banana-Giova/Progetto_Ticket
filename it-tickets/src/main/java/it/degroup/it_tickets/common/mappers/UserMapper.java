package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    RegisterResponse userToRegisterResponse(User user);
}
