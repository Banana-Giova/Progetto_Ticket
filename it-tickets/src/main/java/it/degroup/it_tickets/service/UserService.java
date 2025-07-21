package it.degroup.it_tickets.service;

import it.degroup.it_tickets.presentation.requests.LoginRequest;
import it.degroup.it_tickets.presentation.responses.LoginResponse;

public interface UserService {

    LoginResponse authenticate(LoginRequest request) throws Exception;



}
