package it.degroup.it_tickets.service;

import it.degroup.it_tickets.common.mappers.UserMapper;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.presentation.requests.RegisterRequest;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import it.degroup.it_tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail();
        boolean existingUser = repository.existsByEmail(email);
        if (existingUser) {
            throw new DuplicateException(String.format("Un utente registrato con la seguente mail '%s' già esiste.", email));
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(email, request.getName(), request.getSurname(), hashedPassword);
        User new_user = repository.save(user);
        return UserMapper.INSTANCE.userToRegisterResponse(new_user);
    }



    @Override
    public void confirmEmail(String email_token) {

    }
}
