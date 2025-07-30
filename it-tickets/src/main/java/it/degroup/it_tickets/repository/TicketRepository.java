package it.degroup.it_tickets.repository;

import it.degroup.it_tickets.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
