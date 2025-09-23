package it.degroup.it_tickets.presentation.responses;

import lombok.Data;

import java.util.Map;

@Data
public class TicketChartResponse {
    Map<String, Integer> ticketStats;
}
