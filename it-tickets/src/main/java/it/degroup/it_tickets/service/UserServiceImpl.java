package it.degroup.it_tickets.service;

import it.degroup.it_tickets.presentation.requests.LoginRequest;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.repository.UserRepository;
import it.degroup.it_tickets.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired UserRepository userRepository;
    @Autowired JwtUtil jwtUtil;

    @Override
    public LoginResponse authenticate(LoginRequest request) throws Exception {
        var userFinded = userRepository.findByEmail(request.getEmail());
        if (userFinded.isEmpty())
            throw new NullPointerException("utente non presente nel sistema.");
        if(userFinded.get().checkPassword(request.getPassword())== false)
            throw new Exception("credenziali non valide");

        String tokenResponse = jwtUtil.generateToken(userFinded.get().getEmail());
        //return userFinded.get();
        return new LoginResponse(tokenResponse);
    }

}
