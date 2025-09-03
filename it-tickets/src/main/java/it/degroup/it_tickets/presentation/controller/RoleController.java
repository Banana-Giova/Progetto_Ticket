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

import java.util.List;

@RestController
@RequestMapping("/roles")
@CrossOrigin("*")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping(path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE  )
    public ResponseEntity<OperationResult<RoleResponse>> createRole(
            @Valid @RequestBody CreateRoleRequest request) {
        try {
            RoleResponse response = roleService.createRole(request.getName());
            OperationResult<RoleResponse> result =
                    OperationResult.ok(response, "Creazione ruolo completata con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (RuntimeException e) {
            OperationResult<RoleResponse> result =
                    OperationResult.ko(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @GetMapping(path = "/get",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<RoleResponse>> getRole(
            @RequestParam String roleName) {
        try {
            RoleResponse response = roleService.getRole(roleName);
            OperationResult<RoleResponse> result =
                    OperationResult.ok(response, "Ottenimento dati sul ruolo completato con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (RuntimeException e) {
            OperationResult<RoleResponse> result =
                    OperationResult.ko(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @GetMapping(path = "/get-users-by-role",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<List<ProfileResponse>>> getUsersByRole(
            @RequestParam String roleName) {
        try {
            List<ProfileResponse> response = roleService.getUsersByRole(roleName);
            OperationResult<List<ProfileResponse>> result =
                    OperationResult.ok(response, "Ottenimento lista utenti completato con successo!");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (RuntimeException e) {
            OperationResult<List<ProfileResponse>> result =
                    OperationResult.ko(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
    }

    @PostMapping(path = "/assign",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE  )
    public ResponseEntity<OperationResult<String>> createRole(
            @Valid @RequestBody AssignRoleRequest request) {
        try {
            roleService.assignRoleToUser(request.getEmail(), request.getRoleName());
            OperationResult<String> result =
                    OperationResult.ok("RUOLO ASSEGNATO CORRETTAMENTE ALL'UTENTE CON LA SEGUENTE EMAIL: " + request.getEmail(),
                                     "Assegnazione ruolo completata con successo!");
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
}