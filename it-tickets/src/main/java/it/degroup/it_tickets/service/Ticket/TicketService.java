package it.degroup.it_tickets.service.Ticket;

import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.TicketRequest;

public interface TicketService {

    Ticket addTicket(TicketRequest request, User user);
}
