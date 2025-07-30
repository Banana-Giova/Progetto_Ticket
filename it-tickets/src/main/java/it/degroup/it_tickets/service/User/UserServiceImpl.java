package it.degroup.it_tickets.service.User;

import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.common.mappers.UserMapper;
import it.degroup.it_tickets.common.providers.EmailSender;
import it.degroup.it_tickets.common.security.PasswordHelper;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.*;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import it.degroup.it_tickets.providers.models.EmailConfirmRegistrationTemplateMessage;
import it.degroup.it_tickets.providers.models.ForgotPasswordEmailTemplateMessage;
import it.degroup.it_tickets.repository.UserRepository;
import it.degroup.it_tickets.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private PasswordHelper passwordHelper;

    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${gmail.app.username}")
    private String mailFrom;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        String email = request.getEmail();
        boolean existingUser = repository.existsByEmail(email);
        if (existingUser) {
            throw new DuplicateException(String.format("Un utente registrato con la seguente mail '%s' già esiste.", email));
        } else if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("La password non coincide con il campo conferma password.");
        }
        User user = new User(email, request.getName(), request.getSurname(), passwordHelper.encode(request.getNewPassword())); //devo recupeare l'utente dal db perchè quello recuperato dal ceurity context non nessun legame con il repository
        User new_user = repository.save(user);

        sendEmailToken(email);
        return userMapper.userToRegisterResponse(new_user);
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        var userFound = repository.findByEmail(request.getEmail());
        if (userFound.isEmpty()) {
            throw new UsernameNotFoundException("Utente non presente nel sistema.");
        }
        User user = userFound.get();

        if (!passwordHelper.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Credenziali non valide.");

        String tokenResponse = null;
        try {
            tokenResponse = jwtUtil.generateToken(user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Errore generazione token JWT", e);
        }
        user.setToken(tokenResponse);
        repository.saveAndFlush(user);
        System.out.println("TOKEN SALVATO: " + user.getToken());

        return new LoginResponse(tokenResponse);
    }

    @Override
    public void sendEmailToken(String userEmail) {
        var userFound = repository.findByEmail(userEmail);
        if (userFound.isEmpty()) {
            throw new UsernameNotFoundException("Utente non presente nel sistema.");
        }
        User user = userFound.get();
        String emailToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        user.setEmailToken(emailToken);
        user.setEmailExpiration(expiresAt);
        repository.save(user);

        emailSender.sendEmail(
                user.getEmail(),
                mailFrom,
                "Conferma la tua email",
                EmailConfirmRegistrationTemplateMessage.create(
                        templateEngine,
                        frontendUrl,
                        user,
                        emailToken
                )
        );
    }

    @Override
    public void confirmEmailToken(EmailTokenRequest emailToken) {
        User user = repository.findByEmailToken(emailToken.getToken())
                .orElseThrow(() -> new InvalidOneTimeTokenException("Token invalido"));

        if (user.isEmailTokenExpired(false)) {
            repository.delete(user);
            throw new IllegalStateException("Email token scaduto! Si è pregati di registrare di nuovo l'account");
        }
        user.setEmailConfirmed(true);
        user.setEmailToken(null);
        user.setEmailExpiration(null);
        repository.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        var userFound = repository.findByEmail(request.getUserEmail());
        if (userFound.isEmpty()) {
            throw new UsernameNotFoundException("Utente non presente nel sistema.");
        }
        User user = userFound.get();

        if (!passwordHelper.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Vecchia password errata.");
        } else if (passwordHelper.matches(request.getNewPassword(), user.getPassword()) ||
                   request.getNewPassword().equals(request.getOldPassword())) {
            throw new IllegalArgumentException("La nuova password dev'essere diversa da quella vecchia.");
        }

        user.passwordTokenCheck();

        user.setPassword(passwordHelper.encode(request.getNewPassword()));
        user.setPasswordToken(null);
        user.setEmailExpiration(null);
        repository.save(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        var userFound = repository.findByEmail(request.getUserEmail());
        if (userFound.isEmpty()) {
            throw new UsernameNotFoundException("Utente non presente nel sistema.");
        }
        User user = userFound.get();

        String temp_password = passwordHelper.generateTemporaryPassword(16);
        emailSender.sendEmail(
                user.getEmail(),
                mailFrom,
                "Password dimenticata",
                ForgotPasswordEmailTemplateMessage.create(
                        templateEngine,
                        frontendUrl,
                        user,
                        temp_password
                )
        );
        user.setPassword(passwordHelper.encode(temp_password));
        user.setPasswordToken(user.getPassword());
        user.setPasswordExpiration(LocalDateTime.now().plusHours(1));
        repository.save(user);
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