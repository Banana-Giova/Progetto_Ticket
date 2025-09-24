package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.Notification;
import it.degroup.it_tickets.presentation.responses.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "recipient", source = "recipient.email")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "read", source = "read")
    @Mapping(target = "createdAt", source = "createdAt")
    NotificationResponse notifToResponse(Notification notif);
}


