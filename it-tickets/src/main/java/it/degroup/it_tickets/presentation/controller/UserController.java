package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.presentation.requests.RegisterRequest;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import it.degroup.it_tickets.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService service;

    /*
     *
    Registration Methods
    *
    */

    @PostMapping(path = "/register",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE  )
    public ResponseEntity<OperationResult<RegisterResponse>> register(
           @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse response = service.register(registerRequest);
            OperationResult<RegisterResponse> result =
                    OperationResult.ok(response, "Creazione utente completata con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (DuplicateException | IllegalArgumentException exception) {
            OperationResult<RegisterResponse> result =
                    OperationResult.ko(exception.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @GetMapping(path = "/confirm_email")
    public ResponseEntity<Void> confirmEmail(
            @RequestParam("token") String token) {
        service.confirmEmail(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
