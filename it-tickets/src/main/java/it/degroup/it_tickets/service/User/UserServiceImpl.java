package it.degroup.it_tickets.service.User;
import it.degroup.it_tickets.presentation.requests.LoginRequest;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.repository.UserRepository;
import it.degroup.it_tickets.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.degroup.it_tickets.common.mappers.UserMapper;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.presentation.requests.RegisterRequest;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;


    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail();
        boolean existingUser = repository.existsByEmail(email);
        if (existingUser) {
            throw new DuplicateException(String.format("Un utente registrato con la seguente mail '%s' già esiste.", email));
        } else if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("La password non coincide con il campo conferma password.");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(email, request.getName(), request.getSurname(), hashedPassword);
        User new_user = repository.save(user);
        return userMapper.userToRegisterResponse(new_user);
    }


    @Override
    public void confirmEmail(String email_token) {
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) throws Exception {
        var userFinded = repository.findByEmail(request.getEmail());
        if (userFinded.isEmpty())
            throw new UsernameNotFoundException("utente non presente nel sistema.");
        if (!passwordEncoder.matches(request.getPassword(), userFinded.get().getPassword()))
            throw new Exception("credenziali non valide");
        String tokenResponse = jwtUtil.generateToken(userFinded.get().getEmail());
        userFinded.get().setToken(tokenResponse);
        repository.saveAndFlush(userFinded.get());
        System.out.println("TOKEN salvato: " + userFinded.get().getToken());

        return new LoginResponse(tokenResponse);
    }

    @Override
    public Optional<String> getTokenByEmail(String email) {
        return repository.getTokenByEmail(email);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return repository.findByEmail(email);
    }

}