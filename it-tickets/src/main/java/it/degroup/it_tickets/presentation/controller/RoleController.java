package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.entity.Category;
import it.degroup.it_tickets.presentation.requests.AssignRoleRequest;
import it.degroup.it_tickets.presentation.requests.CreateRoleRequest;
import it.degroup.it_tickets.presentation.responses.RoleResponseWOUsers;
import it.degroup.it_tickets.presentation.responses.UserResponseWithRoles;
import it.degroup.it_tickets.presentation.responses.RoleResponseWithUsers;
import it.degroup.it_tickets.service.Role.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping(path = "/create",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<RoleResponseWithUsers>> createRole(
            @Valid @RequestBody CreateRoleRequest request) {

        RoleResponseWithUsers response = roleService.createRole(request.getName());
        OperationResult<RoleResponseWithUsers> result =
                OperationResult.ok(response, "Creazione ruolo completata con successo!");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @GetMapping(path = "/get-all",
                produces = "application/json")
    public ResponseEntity<?> getAllRoles() {
        List<RoleResponseWOUsers> response = roleService.getAllRoles();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get-one",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<RoleResponseWithUsers>> getRole(
            @RequestParam String roleName) {

        RoleResponseWithUsers response = roleService.getRole(roleName);
        OperationResult<RoleResponseWithUsers> result =
                OperationResult.ok(response, "Ottenimento dati sul ruolo completato con successo!");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @GetMapping(path = "/get-users-by-role",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<List<UserResponseWithRoles>>> getUsersByRole(
            @RequestParam String roleName) {

        List<UserResponseWithRoles> response = roleService.getUsersByRole(roleName);
        OperationResult<List<UserResponseWithRoles>> result =
                OperationResult.ok(response, "Ottenimento lista utenti completato con successo!");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @PostMapping(path = "/assign",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> assignRole(
            @Valid @RequestBody AssignRoleRequest request) {

        roleService.assignRoleToUser(request.getEmail(), request.getRoleName());
        OperationResult<String> result =
                OperationResult.ok(
                        "RUOLO ASSEGNATO CORRETTAMENTE ALL'UTENTE CON LA SEGUENTE EMAIL: " + request.getEmail(),
                        "Assegnazione ruolo completata con successo!"
                );

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @PostMapping(path = "/remove",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OperationResult<String>> removeRole(
            @Valid @RequestBody AssignRoleRequest request) {

        roleService.removeRoleFromUser(request.getEmail(), request.getRoleName());
        OperationResult<String> result =
                OperationResult.ok(
                        "RUOLO RIMOSSO CORRETTAMENTE ALL'UTENTE CON LA SEGUENTE EMAIL: " + request.getEmail(),
                        "Rimozione ruolo completata con successo!"
                );

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }
}
