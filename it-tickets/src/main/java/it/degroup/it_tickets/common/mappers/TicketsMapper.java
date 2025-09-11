package it.degroup.it_tickets.common.mappers;

import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
import it.degroup.it_tickets.presentation.responses.TicketResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketsMapper {
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "is_priority", target = "is_priority")
    @Mapping(source = "status", target = "status") // converte automaticamente l'enum in string
    @Mapping(source = "created_at", target = "created_at")
    @Mapping(source = "modified_at", target = "modified_at")
    TicketResponse toResponse(Ticket ticket);
    List<TicketResponse> toResponseList(List<Ticket> tickets);

    @Mapping(source = "category", target = "category.id")
    Ticket toEntity(TicketRequest request);
}


