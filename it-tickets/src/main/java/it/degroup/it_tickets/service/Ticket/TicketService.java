package it.degroup.it_tickets.service.Ticket;

import it.degroup.it_tickets.entity.Category;
import it.degroup.it_tickets.entity.Status;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
import it.degroup.it_tickets.presentation.responses.TicketChartResponse;
import it.degroup.it_tickets.presentation.responses.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService {

    TicketResponse addTicket(TicketRequest request);
    Page<TicketResponse> findTicketsByUser(Long userId,Pageable pageable);

    Page<TicketResponse> findTicketsByUserWithFilters(
            String keyword,
            String categoryName,
            Status status,
            Pageable pageable);

    List<Category> getAllCategories();

    List<Status> getAllStatuses();

    TicketChartResponse getTicketChart();
}
