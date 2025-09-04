package it.degroup.it_tickets.presentation.controller;
import it.degroup.it_tickets.entity.Status;
import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
import it.degroup.it_tickets.presentation.responses.TicketResponse;
import it.degroup.it_tickets.service.Ticket.TicketService;
import it.degroup.it_tickets.service.User.MyUserDetails;
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
public class TicketController {

    @Autowired
    TicketService service;

    @PostMapping(path = "/add-ticket", produces = "application/json")
    public ResponseEntity<?> addTicket(@RequestBody TicketRequest request){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //recupera l'utente autenticato
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            User user = userDetails.getUser();
            Ticket ticket = service.addTicket(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());


        }

    }

    @GetMapping(path = "/tickets", produces = "application/json")
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

    @GetMapping("/tickets/status")
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok(service.getAllStatuses());
    }




}
