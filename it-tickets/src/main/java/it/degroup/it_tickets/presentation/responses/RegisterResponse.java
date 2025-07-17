package it.degroup.it_tickets.presentation.responses;

import lombok.Data;

@Data
public class RegisterResponse {

    private Long id;
    private String email;
    private String name;
    private String surname;
}
