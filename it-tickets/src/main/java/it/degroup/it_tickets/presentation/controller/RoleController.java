package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.common.exceptions.EmailNotConfirmedException;
import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.presentation.requests.*;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.presentation.responses.ProfileResponse;
import it.degroup.it_tickets.presentation.responses.RoleResponse;
import it.degroup.it_tickets.service.Role.RoleService;
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
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping(path = "/create_role",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE  )
    public ResponseEntity<OperationResult<RoleResponse>> register(
            @Valid @RequestBody CreateRoleRequest request) {
        try {
            RoleResponse response = roleService.createRole(request.getName());
            OperationResult<RoleResponse> result =
                    OperationResult.ok(response, "Creazione ruolo completata con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (DuplicateException | IllegalArgumentException e) {
            OperationResult<RoleResponse> result =
                    OperationResult.ko(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }
}