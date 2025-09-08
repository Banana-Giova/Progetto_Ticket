package it.degroup.it_tickets.service.Ticket;

import it.degroup.it_tickets.common.mappers.TicketsMapper;
import it.degroup.it_tickets.entity.*;
//import it.degroup.it_tickets.presentation.requests.PaginationRequest;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
//import it.degroup.it_tickets.presentation.responses.PagingResult;
import it.degroup.it_tickets.presentation.responses.TicketResponse;
import it.degroup.it_tickets.repository.CategoryRepository;
import it.degroup.it_tickets.repository.TicketRepository;
import it.degroup.it_tickets.repository.UserRepository;
//import it.degroup.it_tickets.utility.PaginationUtils;
import it.degroup.it_tickets.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public Ticket addTicket(TicketRequest request, User userFromSecurityContext) {
        Optional<Category> category = categoryRepository.findById(request.getCategory());
        if (category.isEmpty())
            throw new IllegalArgumentException("categoria non presente");
        Optional<User> user = userRepository.findByEmail(userFromSecurityContext.getEmail());
        if(user.isEmpty()) {
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
    public Page<TicketResponse> findTicketsByUserWithFilters(
            Long userId,
            String keyword,
            String categoryName,
            Status status,
            Pageable pageable) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utente non trovato");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.get().getRoles() == null || user.get().getRoles().isEmpty()) {
            throw new RuntimeException("Utente senza ruoli, accesso negato");
        }
        Page<Ticket> tickets;

        if (user.get().getRoles()
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

            return tickets.map(mapper::toResponse);
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

            return tickets.map(mapper::toResponse);
        }
    }



    @Override
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Override
    public List<Status> getAllStatuses() {
        return List.of(Status.values());
    }


//    @Override
//    public Page<TicketResponse> findTicketsByUser(Long userId, Pageable pageable) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
//
//        if (user.getRoles() == null || user.getRoles().isEmpty()) {
//            throw new RuntimeException("Utente senza ruoli, accesso negato");
//        }
//
//        if (user.getRoles()
//                .stream()
//                .map(Role::getName)
//                .anyMatch("Utente"::equals)) {
//            Page<Ticket> tickets = ticketRepository.findByUserId(userId, pageable);
//            return tickets.map(mapper::toResponse);
//        } else {
//            Page<Ticket> tickets = ticketRepository.findAll(pageable);
//            return tickets.map(mapper::toResponse);
//
//        }
//
//    }
    



}
