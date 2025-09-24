package it.degroup.it_tickets.presentation.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private Boolean is_priority;
    private String categoryName;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;
    private String status; // se hai un enum Status
    private Boolean isAllTickets;
}
