package it.degroup.it_tickets.service.Ticket;

import it.degroup.it_tickets.entity.Category;
import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
import it.degroup.it_tickets.repository.CategoryRepository;
import it.degroup.it_tickets.repository.TicketRepository;
import it.degroup.it_tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        return ticketRepository.save(ticket);
    }
}
