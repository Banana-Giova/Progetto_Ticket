package it.degroup.it_tickets.service.User;

import it.degroup.it_tickets.common.exceptions.DuplicateException;
import it.degroup.it_tickets.common.mappers.UserMapper;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.LoginRequest;
import it.degroup.it_tickets.presentation.requests.RegisterRequest;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.presentation.responses.RegisterResponse;
import it.degroup.it_tickets.repository.UserRepository;
import it.degroup.it_tickets.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private JavaMailSender mailSender;

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
        } else if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("La password non coincide con il campo conferma password.");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(email, request.getName(), request.getSurname(), hashedPassword);
        User new_user = repository.save(user);

        // sendEmailToken(email);
        return userMapper.userToRegisterResponse(new_user);
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

        Context ctx = new Context();
        ctx.setVariable("name", user.getName());
        ctx.setVariable("confirmLink", frontendUrl + "/confirm_email?token=" + emailToken);
        String body = templateEngine.process("confirm-email", ctx);

        createHTMLEmail(user.getEmail(), "Conferma la tua email!", body);
    }

    @Override
    public void confirmEmailToken(String emailToken) {
        User user = repository.findByEmailToken(emailToken)
                .orElseThrow(() -> new InvalidOneTimeTokenException("Token non valido"));

        if (user.getEmailExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token scaduto! Si è pregati di generarne un altro");
        }
        user.setEmailConfirmed(true);
        user.setEmailToken(null);
        user.setEmailExpiration(null);
        repository.save(user);
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) throws Exception {
        var userFinded = repository.findByEmail(request.getEmail());
        if (userFinded.isEmpty())
            throw new UsernameNotFoundException("utente non presente nel sistema.");
        if (userFinded.get().checkPassword(request.getPassword()) == false)
            throw new Exception("credenziali non valide");

        String tokenResponse = jwtUtil.generateToken(userFinded.get().getEmail());
        return new LoginResponse(tokenResponse);
    }

    // Da riposizionare
    public void createHTMLEmail(String to, String subject, String htmlBody) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
        };
        // MIME = Multipurpose Internet Mail Extensions -> Standard più avanzato di SMTP
        try {
            mailSender.send(preparator);
        } catch (MailException e) {
            throw new RuntimeException("Errore durante l'invio dell'email: ", e);
        }
    }
}