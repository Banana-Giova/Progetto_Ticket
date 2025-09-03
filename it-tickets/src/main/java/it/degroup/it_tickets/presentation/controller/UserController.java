package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.common.exceptions.EmailNotConfirmedException;
import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.presentation.requests.*;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.presentation.responses.ProfileResponse;
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

    @GetMapping(path = "/test")
    public ResponseEntity<Void> test() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping (path = "/login", consumes = "application/json")   //responseEntity è una classe generica per gestire le risposte hhtp
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException | BadCredentialsException | EmailNotConfirmedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore interno: " + e.getMessage());
        }
    }

    @PostMapping(path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE  )
    public ResponseEntity<OperationResult<ProfileResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            ProfileResponse response = userService.register(request);
            OperationResult<ProfileResponse> result =
                    OperationResult.ok(response, "Creazione utente completata con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (DuplicateException | IllegalArgumentException e) {
            OperationResult<ProfileResponse> result =
                    OperationResult.ko(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @PostMapping(path = "/email-confirmation",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> confirmEmail(
            @Valid @RequestBody EmailTokenRequest request) {
        try {
            userService.confirmEmailToken(request);
            OperationResult<String> result =
                    OperationResult.ok("TOKEN VALIDO", "Conferma token avvenuta con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        } catch (InvalidOneTimeTokenException | IllegalStateException e) {
            OperationResult<String> result =
                    OperationResult.ko(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @PostMapping(path = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            OperationResult<String> result =
                    OperationResult.ok("RESET PASSWORD VALIDO", "Reset password avvenuto con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (UsernameNotFoundException | BadCredentialsException | IllegalArgumentException e) {
            OperationResult<String> result =
                    OperationResult.ko(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @PostMapping(path = "/forgot-password",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> resetPassword(
            @Valid @RequestBody OnlyEmailRequest request) {
        try {
            userService.forgotPassword(request);
            OperationResult<String> result =
                    OperationResult.ok("MAIL CON PASSWORD TEMPORANEA INVIATA", "Password temporanea generata ed inviata con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (RuntimeException e) {
            OperationResult<String> result =
                    OperationResult.ko(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @PostMapping(path = "/profile_fetch",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<ProfileResponse>> profileFetch (
            @Valid @RequestBody OnlyEmailRequest request) {
       try {
            ProfileResponse response = userService.profileFetch(request);
            OperationResult<ProfileResponse> result =
                    OperationResult.ok(response, "Fetch del profile utente eseguito con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (RuntimeException e) {
            OperationResult<ProfileResponse> result =
                    OperationResult.ko(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }
}