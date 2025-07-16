package it.degroup.it_tickets.dao;

import it.degroup.it_tickets.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketDAO extends JpaRepository<Ticket, Long> {

}
