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
import it.degroup.it_tickets.service.User.MyUserDetails;
import it.degroup.it_tickets.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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



    @Override
    public TicketResponse addTicket(TicketRequest request) {
        Optional<Category> category = categoryRepository.findById(request.getCategory());
        if (category.isEmpty())
            throw new IllegalArgumentException("Categoria non presente.");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        Ticket ticket = new Ticket();
        ticket.setCategory(category.get());
        ticket.setDescription(request.getDescription());
        ticket.setUser(user);
        ticket.setTitle(request.getTitle());
        ticket.setIs_priority(request.getIs_priority());
        ticket.setCreated_at(LocalDateTime.now());
        ticket.setStatus(Status.TO_DO);

        return mapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    public Page<TicketResponse> findTicketsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId))
            throw new RuntimeException("Utente non trovato.");

        Page<Ticket> tickets = ticketRepository.findByUserId(userId, pageable);

        return tickets.map(mapper::toResponse);
    }

    @Override
    public Page<TicketResponse> findTicketsByUserWithFilters(
            String keyword,
            String categoryName,
            Status status,
            Pageable pageable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        Long userId = user.getId();
        if (user == null || user.getRoles().isEmpty())
            throw new RuntimeException("Utente nullo o illegale, accesso negato.");

        Page<Ticket> tickets;

        if (user.getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch("Utente"::equals)) {
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

        } else {
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
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Override
    public List<Status> getAllStatuses() {
        return List.of(Status.values());
    }

    @Override
    public TicketChartResponse getTicketChart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //recupera l'utente autenticato
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        if (user == null || user.getRoles().isEmpty())
            throw new RuntimeException("Utente nullo o illegale, accesso negato.");

        Page<Ticket> ticketsPage;
        ticketsPage = ticketRepository.findByUserId(user.getId(), Pageable.unpaged());

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
