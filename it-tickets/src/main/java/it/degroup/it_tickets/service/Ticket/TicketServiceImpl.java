package it.degroup.it_tickets.service.Ticket;

import it.degroup.it_tickets.common.mappers.TicketsMapper;
import it.degroup.it_tickets.entity.*;
//import it.degroup.it_tickets.presentation.requests.PaginationRequest;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
//import it.degroup.it_tickets.presentation.responses.PagingResult;
import it.degroup.it_tickets.presentation.responses.TicketChartResponse;
import it.degroup.it_tickets.presentation.responses.TicketResponse;
import it.degroup.it_tickets.repository.CategoryRepository;
import it.degroup.it_tickets.repository.TicketRepository;
import it.degroup.it_tickets.repository.UserRepository;
//import it.degroup.it_tickets.utility.PaginationUtils;
import it.degroup.it_tickets.service.Category.CategoryService;
import it.degroup.it_tickets.service.Notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class TicketServiceImpl implements TicketService{

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TicketsMapper mapper;
    @Autowired
    CategoryService categoryService;
    @Autowired
    NotificationService notifService;


    @Override
    public Ticket addTicket(TicketRequest request, User userFromSC) {
        Optional<Category> category = categoryRepository.findById(request.getCategory());
        if (category.isEmpty())
            throw new IllegalArgumentException("categoria non presente");
        Optional<User> user = userRepository.findByEmail(userFromSC.getEmail());
        if (user.isEmpty()) {
            throw new RuntimeException("Utente non trovato");
        }

        Ticket ticket = new Ticket();
        ticket.setCategory(category.get());
        ticket.setDescription(request.getDescription());
        ticket.setUser(user.get());
        ticket.setTitle(request.getTitle());
        ticket.setIs_priority(request.getIs_priority());
        ticket.setCreated_at(LocalDateTime.now());
        ticket.setStatus(Status.TO_DO);

        notifService.notifyAllOperators("Ticket creato da " + user.get().getEmail());
        return ticketRepository.save(ticket);
    }

    @Override
    public Page<TicketResponse> findTicketsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId))
            throw new RuntimeException("Utente non trovato");

        Page<Ticket> tickets = ticketRepository.findByUserId(userId, pageable);

        return tickets.map(mapper::toResponse);
    }

    @Override
    public TicketResponse findTicketById(Long id) {
        return ticketRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trovato con l'id " + id));
    }

    @Override
    public Page<TicketResponse> findTicketsByUserWithFilters(
            Long userId,
            String keyword,
            String categoryName,
            Status status,
            Boolean isAllTickets,
            Pageable pageable) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utente non trovato");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.get().getRoles() == null || user.get().getRoles().isEmpty()) {
            throw new RuntimeException("Utente senza ruoli, accesso negato");
        }

        Page<Ticket> tickets = Page.empty();

        if (user.get().getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch("Utente"::equals) && !isAllTickets){
            if (keyword != null && !keyword.isBlank()) {
                tickets = ticketRepository
                        .findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndDescriptionContainingIgnoreCase(
                                userId, keyword, userId, keyword, pageable);

            } else if (categoryName != null && !categoryName.isBlank() && status != null) {
                tickets = ticketRepository.findByUserIdAndCategoryNameAndStatus(userId, categoryName, status, pageable);

            } else if (categoryName != null && !categoryName.isBlank()) {
                tickets = ticketRepository.findByUserIdAndCategoryName(userId, categoryName, pageable);

            } else if (status != null) {
                tickets = ticketRepository.findByUserIdAndStatus(userId, status, pageable);

            } else {
                tickets = ticketRepository.findByUserId(userId, pageable);
            }

        } else if (user.get().getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch("Operatore"::equals) && isAllTickets){
            if (keyword != null && !keyword.isBlank()) {
                tickets = ticketRepository
                        .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                                keyword, keyword, pageable);
            } else if (categoryName != null && !categoryName.isBlank() && status != null) {
                tickets = ticketRepository.findByCategoryNameAndStatus(categoryName, status, pageable);

            } else if (categoryName != null && !categoryName.isBlank()) {
                tickets = ticketRepository.findByCategoryName(categoryName, pageable);

            } else if (status != null) {
                tickets = ticketRepository.findByStatus(status, pageable);

            } else {
                tickets = ticketRepository.findAll(pageable);
            }

        }
        return tickets.map(mapper::toResponse);
    }

    @Override
    public Ticket updateDescription(Long id, String newDescription) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket non trovato con l'id " + id));
        if (!"TO_DO".equals(ticket.getStatus().toString()))
            throw new IllegalStateException("ticket non modificabile perchè gia in lavorazione");
        ticket.setDescription(newDescription);
        ticket.setModified_at(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateStatus(Long id, String status) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket non trovato con l'id " + id));
        ticket.setStatus(Status.valueOf(status));
        ticket.setModified_at(LocalDateTime.now());
        Ticket ticketsaved = ticketRepository.save(ticket);
        return ticketsaved;
    }

    @Override
    public Ticket updateStatusDeleted(Long id, String status) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket non trovato con l'id " + id));
        if (ticket.getStatus().toString().equals("TO_DO")) {
            ticket.setStatus(Status.valueOf(status));
            ticket.setModified_at(LocalDateTime.now());

        }
            return ticketRepository.save(ticket);
    }


    @Override
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Override
    public List<Status> getAllStatuses() {
        return List.of(Status.values());
    }

    @Override
    public TicketChartResponse getTicketChart(User userFromSC) {
        Optional<User> user = userRepository.findByEmail(userFromSC.getEmail());
        if (user.isEmpty()) {
            throw new RuntimeException("Utente non trovato");
        }

        Page<Ticket> ticketsPage;
        if (user.get().isAdmin()) {
            ticketsPage = ticketRepository.findAll(Pageable.unpaged());
        } else {
            ticketsPage = ticketRepository.findByUserId(user.get().getId(), Pageable.unpaged());
        }

        Map<String, Integer> stats = new HashMap<>();
        for (Ticket t : ticketsPage.getContent()) {
            String key = t.getStatus() != null ? t.getStatus().name() : "UNKNOWN";
            stats.put(key, stats.getOrDefault(key, 0) + 1);
        }

        TicketChartResponse response = new TicketChartResponse();
        response.setTicketStats(stats);
        return response;
    }
}
