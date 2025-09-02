package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    ProfileResponse userToProfileResponse(User user);
}
