package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.presentation.requests.RegisterRequest;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import it.degroup.it_tickets.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class UserController {

    @Autowired
    private UserService service;

    /*
     *
    Registration Methods
    *
    */

    @PostMapping(path = "/register", consumes = "application/json")
    public OperationResult<RegisterResponse> register(
           @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse response = service.register(registerRequest);
            return OperationResult.ok(response, "Creazione utente completata con successo!");
        } catch (DuplicateException duplicateException) {
            return OperationResult.ko(duplicateException.getMessage());
        }
    }

    @GetMapping(path = "/confirm_email")
    public ResponseEntity<Void> confirmEmail(
            @RequestParam("token") String token) {
        service.confirmEmail(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
