package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.presentation.requests.*;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.presentation.responses.UserResponseWithRoles;
import it.degroup.it_tickets.service.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/test")
    public ResponseEntity<Void> test() {
        return ResponseEntity.ok().build();
    }

    @PostMapping (path = "/login", consumes = "application/json")   //responseEntity è una classe generica per gestire le risposte hhtp
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/register",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<UserResponseWithRoles>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponseWithRoles response = userService.register(request);
        OperationResult<UserResponseWithRoles> result = OperationResult.ok(response, "Creazione utente completata con successo!");

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @PostMapping(path = "/email-confirmation",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> confirmEmail(
            @Valid @RequestBody EmailTokenRequest request) {
        userService.confirmEmailToken(request);
        OperationResult<String> result = OperationResult.ok("TOKEN VALIDO",
                                                          "Conferma token avvenuta con successo!");

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @PostMapping(path = "/reset-password",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        OperationResult<String> result = OperationResult.ok("RESET PASSWORD VALIDO",
                                                          "Reset password avvenuto con successo!");

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @PostMapping(path = "/forgot-password",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> forgotPassword(
            @Valid @RequestBody OnlyEmailRequest request) {
        userService.forgotPassword(request);
        OperationResult<String> result = OperationResult.ok("MAIL CON PASSWORD TEMPORANEA INVIATA",
                                                          "Password temporanea generata ed inviata con successo!");

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @GetMapping(path = "/profile-fetch",
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<UserResponseWithRoles>> profileFetch() {
        UserResponseWithRoles response = userService.profileFetch();
        OperationResult<UserResponseWithRoles> result = OperationResult.ok(response, "Fetch del profile utente eseguito con successo!");

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }
}
