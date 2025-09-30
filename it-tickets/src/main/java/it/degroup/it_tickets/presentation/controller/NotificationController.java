package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.common.models.OperationResult;
import it.degroup.it_tickets.presentation.responses.NotificationResponse;
import it.degroup.it_tickets.service.Notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notifService;

    @PatchMapping(path = "/read")
    public ResponseEntity<OperationResult<String>> markAsRead(
            @RequestBody Long notifId) {
        notifService.markasRead(notifId);
        OperationResult<String> result = OperationResult.ok("NOTIFICA LETTA",
                "Lettura notifica completata con successo.");

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @GetMapping(path = "/{email}/pending")
    public ResponseEntity<List<NotificationResponse>> getPending(
            @PathVariable("email") String userEmail) {
        List<NotificationResponse> pending = notifService.getPending(userEmail);

        return ResponseEntity.ok(pending);
    }

    @GetMapping(path = "/{email}/already-read")
    public ResponseEntity<List<NotificationResponse>> getAlreadyRead(
            @PathVariable("email") String userEmail) {
        List<NotificationResponse> alreadyRead = notifService.getRead(userEmail);

        return ResponseEntity.ok(alreadyRead);
    }
}
