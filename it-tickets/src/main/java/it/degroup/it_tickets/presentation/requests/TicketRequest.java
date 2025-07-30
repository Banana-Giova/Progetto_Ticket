package it.degroup.it_tickets.presentation.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    private String title;
    private String description;
    private Boolean is_priority;
    private Long category;
}
