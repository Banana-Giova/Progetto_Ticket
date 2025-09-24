package it.degroup.it_tickets.presentation.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Long id;

    private String recipient;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}
