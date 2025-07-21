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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/login")
public class UserController {
    @Autowired
    private UserService userService;

   @PostMapping (consumes = "application/json")   //responseEntity è una classe generica per gestire le risposte hhtp
   public OperationResult<LoginResponse> login(@RequestBody LoginRequest request) {
       try {
           LoginResponse response = userService.authenticate(request);
           return OperationResult.ok(response, "login avvenuto");
       } catch (UsernameNotFoundException | BadCredentialsException e) {
            return OperationResult.ko("credenziali non valide");
       } catch (Exception e) {
           return OperationResult.ko("Errore del server");
       }


   }
   }