package it.degroup.it_tickets.service.User;

import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.common.exceptions.EmailNotConfirmedException;
import it.degroup.it_tickets.common.mappers.UserMapper;
import it.degroup.it_tickets.common.providers.EmailSender;
import it.degroup.it_tickets.common.security.PasswordHelper;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.*;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.presentation.responses.UserResponseWithRoles;
import it.degroup.it_tickets.providers.models.EmailConfirmRegistrationTemplateMessage;
import it.degroup.it_tickets.providers.models.ForgotPasswordEmailTemplateMessage;
import it.degroup.it_tickets.repository.UserRepository;
import it.degroup.it_tickets.security.JwtUtil;
import it.degroup.it_tickets.service.Role.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.lang.reflect.MalformedParametersException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleService roleService;
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
    public UserResponseWithRoles register(RegisterRequest request) {
        String email = request.getEmail();
        log.info("Password selezionata: " + request.getNewPassword());
        boolean existingUser = userRepository.existsByEmail(email);
        if (existingUser) {
            throw new DuplicateException("Esiste già un account associato a questa email.");
        } else if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("La password non coincide con il campo conferma password.");
        }
        User user = new User(email, request.getName(), request.getSurname(), passwordHelper.encode(request.getNewPassword()));
        User new_user = userRepository.save(user);
        roleService.assignRoleToUser(email, "Utente");

        sendEmailToken(email);
        return userMapper.userToUserResponseWithRoles(new_user);
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        var userFound = userRepository.findByEmail(request.getEmail());
        if (userFound.isEmpty()) {
            throw new UsernameNotFoundException("Utente non presente nel sistema.");
        }
        User user = userFound.get();

        if (!user.getEmailConfirmed()) {
            throw new EmailNotConfirmedException(
                    "L'email dell'account non è ancora stata verificata."
            );
        } else if (!passwordHelper.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Credenziali non valide.");

        String tokenResponse;
        try {
            tokenResponse = jwtUtil.generateToken(user);
            if (tokenResponse.isEmpty() || tokenResponse.isBlank())
                throw new RuntimeException("Token nullo!");
        } catch (Exception e) {
            throw new RuntimeException("Errore generazione token JWT", e);
        }
        user.setToken(tokenResponse);
        userRepository.saveAndFlush(user);
        System.out.println("TOKEN SALVATO: " + user.getToken());

        return new LoginResponse(tokenResponse,
                                 user.getId(), user.getEmail(),
                                 user.getName(), user.getSurname(),
                                 user.getRoleList());
    }

    @Override
    public void sendEmailToken(String userEmail) {
        var userFound = userRepository.findByEmail(userEmail);
        if (userFound.isEmpty()) {
            throw new UsernameNotFoundException("Utente non presente nel sistema.");
        }
        User user = userFound.get();
        String emailToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        user.setEmailToken(emailToken);
        user.setEmailExpiration(expiresAt);
        userRepository.save(user);

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
        User user = userRepository.findByEmailToken(emailToken.getToken())
                .orElseThrow(() -> new InvalidOneTimeTokenException("Token invalido"));

        if (user.isEmailTokenExpired(false)) {
            userRepository.delete(user);
            throw new IllegalStateException("Email token scaduto! Si è pregati di registrare di nuovo l'account");
        }
        user.setEmailConfirmed(true);
        user.setEmailToken(null);
        user.setEmailExpiration(null);
        userRepository.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Optional<User> userFound;
        boolean isAuthed;
        if (request.getUserEmail().isBlank()) {
            userFound = userRepository.findByPasswordToken(request.getPasswordToken());
            isAuthed = false;
        } else if (request.getPasswordToken().isBlank()) {
            userFound = userRepository.findByEmail(request.getUserEmail());
            isAuthed = true;
        } else {
            throw new MalformedParametersException("Richiesta malformata.");
        }
        if (userFound.isEmpty())
            throw new UsernameNotFoundException("Utente non presente nel sistema.");

        User user = userFound.get();

        if (!passwordHelper.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Vecchia password errata.");
        } else if (passwordHelper.matches(request.getNewPassword(), user.getPassword()) ||
                   request.getNewPassword().equals(request.getOldPassword())) {
            throw new IllegalArgumentException("La nuova password dev'essere diversa da quella vecchia.");
        }

        if (!isAuthed)
            if (!user.passwordTokenCheck(request.getPasswordToken()))
                throw new InvalidOneTimeTokenException("Password token invalido!");

        user.setPassword(passwordHelper.encode(request.getNewPassword()));
        user.setPasswordToken(null);
        user.setPasswordExpiration(null);
        log.info("Nuova password utente: {}", request.getNewPassword());

        userRepository.save(user);
    }

    @Override
    public void forgotPassword(OnlyEmailRequest request) {
        var userFound = userRepository.findByEmail(request.getUserEmail());
        if (userFound.isEmpty()) {
            throw new UsernameNotFoundException("Utente non presente nel sistema.");
        }
        User user = userFound.get();

        String tempPassword = passwordHelper.generateTemporaryPassword(16);
        String passwordToken = UUID.randomUUID().toString();
        emailSender.sendEmail(
                user.getEmail(),
                mailFrom,
                "Password dimenticata",
                ForgotPasswordEmailTemplateMessage.create(
                        templateEngine,
                        frontendUrl,
                        user,
                        request.getUserEmail(),
                        tempPassword,
                        passwordToken
                )
        );
        user.setPassword(passwordHelper.encode(tempPassword));
        user.setPasswordToken(passwordToken);
        user.setPasswordExpiration(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
    }

    @Override
    public UserResponseWithRoles profileFetch() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        return userMapper.userToUserResponseWithRoles(user);
    }

    @Override
    public Optional<String> getTokenByEmail(String email) {
        return userRepository.getTokenByEmail(email);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}