package it.degroup.it_tickets.service.Ticket;

import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.TicketRequest;

import java.util.List;

public interface TicketService {

    Ticket addTicket(TicketRequest request, User user);
    List<Ticket> findTicketsByUser(Long userId);
}
