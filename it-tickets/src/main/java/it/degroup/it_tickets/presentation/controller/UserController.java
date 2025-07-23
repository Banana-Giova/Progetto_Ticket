package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.presentation.requests.LoginRequest;
import it.degroup.it_tickets.presentation.requests.NewEmailTokenRequest;
import it.degroup.it_tickets.presentation.requests.RegisterRequest;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import it.degroup.it_tickets.service.User.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

   @PostMapping (path = "/login")   //responseEntity è una classe generica per gestire le risposte hhtp
   public ResponseEntity<?> login(@RequestBody LoginRequest request) {
       try {
           LoginResponse response = userService.authenticate(request);
           return ResponseEntity.ok(response);
       } catch (UsernameNotFoundException | BadCredentialsException e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Errore interno: " + e.getMessage());
       }
   }

    @PostMapping(path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE  )
    public ResponseEntity<OperationResult<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse response = userService.register(registerRequest);
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
    public ResponseEntity<OperationResult<String>> confirmEmail(
            @RequestParam("token") String token) {
        try {
            userService.confirmEmailToken(token);
            OperationResult<String> result =
                    OperationResult.ok("TOKEN VALIDO", "Conferma token avvenuta con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (InvalidOneTimeTokenException | IllegalStateException exception) {
            OperationResult<String> result =
                    OperationResult.ko(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @PostMapping(path = "/new_email_token",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> newEmailToken(
            @RequestBody NewEmailTokenRequest newTokenRequest) {
        try {
            userService.sendEmailToken(newTokenRequest.getEmail());
            OperationResult<String> result =
                    OperationResult.ok("TOKEN INVIATO", "Invio nuovo token avvenuto con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (RuntimeException exception) {
            OperationResult<String> result =
                    OperationResult.ko(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }
}