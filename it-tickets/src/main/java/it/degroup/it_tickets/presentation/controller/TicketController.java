package it.degroup.it_tickets.presentation.controller;
import it.degroup.it_tickets.common.mappers.TicketsMapper;
import it.degroup.it_tickets.common.security.AuthenticationHelper;
import it.degroup.it_tickets.entity.Status;
import it.degroup.it_tickets.entity.Ticket;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.PatchRequestStatus;
import it.degroup.it_tickets.presentation.requests.PatchStatusDeleted;
import it.degroup.it_tickets.presentation.requests.TicketRequest;
import it.degroup.it_tickets.presentation.requests.UpdateDescriptionRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Autowired
    AuthenticationHelper authHelper;

    @PostMapping(path = "/add", produces = "application/json")
    public ResponseEntity<?> addTicket(@RequestBody TicketRequest request){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //recupera l'utente autenticato
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            User user = userDetails.getUser();
            Ticket ticket = service.addTicket(request, user);
            TicketResponse response = mapper.toResponse(ticket);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        TicketResponse ticket = service.findTicketById(id);
        return ResponseEntity.ok(ticket);
    }


    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<?> getAllTicketsByUser(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Boolean isAllTickets,
            Pageable pageable){
        User user = authHelper.getUser();

        Page<TicketResponse> tickets = service.findTicketsByUserWithFilters(
                user.getId(), keyword, categoryName, status, isAllTickets, pageable);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status")
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok(service.getAllStatuses());
    }

    @GetMapping("/chart")
    public ResponseEntity<TicketChartResponse> getTicketChart() {
        User user = authHelper.getUser();

        return ResponseEntity.ok(service.getTicketChart(user));
    }

    @PreAuthorize("hasRole('Utente')")
    @PatchMapping(path= "/{id}/description")
    public ResponseEntity<?> updateDescription(@PathVariable Long id, @RequestBody UpdateDescriptionRequest request) {
        try {
            Ticket ticket = service.updateDescription(id, request.getNewDescription());
            TicketResponse response = mapper.toResponse(ticket);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('Operatore')")
    @PatchMapping(path = "/{id}/statuschange", consumes ="application/json")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody PatchRequestStatus request) {
            Ticket ticket = service.updateStatus(id, request.getNewStatus());
            TicketResponse response = mapper.toResponse(ticket);
            return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('Utente')")
    @PatchMapping(path = "/{id}/delete-status", consumes = "application/json")
    public ResponseEntity<?> updateStatusDeleted(@PathVariable Long id, @RequestBody PatchStatusDeleted request) {
        Ticket ticket = service.updateStatusDeleted(id, request.getDeletedStatus());
        TicketResponse response = mapper.toResponse(ticket);
        return ResponseEntity.ok(response);
    }


}

