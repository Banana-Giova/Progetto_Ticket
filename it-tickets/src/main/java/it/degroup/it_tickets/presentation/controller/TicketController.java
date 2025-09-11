package it.degroup.it_tickets.presentation.controller;
import it.degroup.it_tickets.common.mappers.TicketsMapper;
import it.degroup.it_tickets.entity.Status;
import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
import it.degroup.it_tickets.presentation.responses.TicketChartResponse;
import it.degroup.it_tickets.presentation.responses.TicketResponse;
import it.degroup.it_tickets.service.Ticket.TicketService;
import it.degroup.it_tickets.service.User.MyUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/tickets")
public class TicketController {

    @Autowired
    TicketService service;
    @Autowired
    TicketsMapper mapper;

    @PostMapping(path = "/add", produces = "application/json")
    public ResponseEntity<?> addTicket(@RequestBody TicketRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //recupera l'utente autenticato
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        Ticket ticket = service.addTicket(request, user);
        TicketResponse response = mapper.toResponse(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        try {
            TicketResponse ticket = service.findTicketById(id);
            return ResponseEntity.ok(ticket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<?> getAllTicketsByUser(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Status status,
            Pageable pageable){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //recupera l'utente autenticato
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            User user = userDetails.getUser();

            Page<TicketResponse> tickets = service.findTicketsByUserWithFilters(
                    user.getId(), keyword, categoryName, status, pageable);

            return ResponseEntity.ok(tickets);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        }
    }



    @GetMapping("/status")
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok(service.getAllStatuses());
    }

    @GetMapping("/chart")
    public ResponseEntity<TicketChartResponse> getTicketChart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //recupera l'utente autenticato
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        return ResponseEntity.ok(service.getTicketChart(user));
    }

}
