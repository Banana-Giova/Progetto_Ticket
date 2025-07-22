package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.presentation.requests.LoginRequest;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
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


   }
