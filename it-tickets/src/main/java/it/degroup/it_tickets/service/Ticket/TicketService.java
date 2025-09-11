package it.degroup.it_tickets.service.Ticket;

import it.degroup.it_tickets.entity.Category;
import it.degroup.it_tickets.entity.Status;
import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
import it.degroup.it_tickets.presentation.responses.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketService {

    Ticket addTicket(TicketRequest request, User user);
    Page<TicketResponse> findTicketsByUser(Long userId,Pageable pageable);
    TicketResponse findTicketById(Long id);
    Page<TicketResponse> findTicketsByUserWithFilters(
            Long userId,
            String keyword,
            String categoryName,
            Status status,
            Pageable pageable);


    List<Category> getAllCategories();

    List<Status> getAllStatuses();


}
