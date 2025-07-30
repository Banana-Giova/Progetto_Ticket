package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
import it.degroup.it_tickets.service.Ticket.TicketService;
import it.degroup.it_tickets.service.User.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketController {

    @Autowired
    TicketService service;

    @PostMapping(path = "/add-ticket", produces = "application/json")
    public ResponseEntity<?> addTicket(@RequestBody TicketRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //recupera l'utente autenticato
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        Ticket ticket = service.addTicket(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
