package it.degroup.it_tickets.service;

import it.degroup.it_tickets.presentation.requests.RegisterRequest;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;

public interface UserService {

    RegisterResponse register(RegisterRequest dto);

    void confirmEmail(String email_token);
}