package it.degroup.it_tickets.service.Notification;

import it.degroup.it_tickets.common.mappers.NotificationMapper;
import it.degroup.it_tickets.common.providers.EmailSender;
import it.degroup.it_tickets.common.security.AuthenticationHelper;
import it.degroup.it_tickets.entity.Notification;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.NotificationResponse;
import it.degroup.it_tickets.providers.models.NotifyOfflineUserTemplateMessage;
import it.degroup.it_tickets.repository.NotificationRepository;
import it.degroup.it_tickets.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notifRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private NotificationMapper notifMapper;
    @Autowired
    private AuthenticationHelper authHelper;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${gmail.app.username}")
    private String mailFrom;

    @Override
    public NotificationResponse notifyUser(String recipientEmail, String message) {
        User currUser = userRepo.findByEmail(recipientEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + recipientEmail));

        Notification newNotif = Notification.builder()
                .recipient(currUser)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        newNotif = notifRepo.save(newNotif);
        NotificationResponse notifResp = notifMapper.notifToResponse(newNotif);

        if (currUser.getToken().isEmpty()) {
            this.emailOfflineUser(recipientEmail, message);
            return notifResp;
        }
        messagingTemplate.convertAndSendToUser(
                recipientEmail,
                "/queue/notifications",
                notifResp);

        return notifResp;
    }

    @Override
    public void notifyAllOperators(String message) {
        List<User> operators = userRepo.findAlsoOperatorsList();

        for (User operator : operators) {
            Notification newNotif = Notification.builder()
                    .recipient(operator)
                    .message(message)
                    .read(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            newNotif = notifRepo.save(newNotif);
            NotificationResponse notifResp = notifMapper.notifToResponse(newNotif);

            try {
                if (operator.getToken() == null) {
                    emailOfflineUser(operator.getEmail(), message);
                } else {
                    messagingTemplate.convertAndSendToUser(
                            operator.getEmail(),
                            "/queue/notifications",
                            notifResp
                    );
                }
            } catch (Exception e) {
                String errorText = "WS send error for operator " + operator.getEmail() + "\n=> " + e;
                log.error(errorText);
            }
        }
    }

    @Override
    public NotificationResponse markasRead(Long id) {
        Optional<Notification> optNotif = notifRepo.findById(id);
        if (optNotif.isEmpty())
            throw new IllegalArgumentException("Notifica con ID " + id + " inesistente.");
        if (optNotif.get().isRead())
            throw new IllegalArgumentException("Notifica con ID " + id + " già letta.");
        Notification notif = optNotif.get();
        notif.setRead(true);
        notifRepo.save(notif);

        return notifMapper.notifToResponse(notif);
    }

    @Override
    public void emailOfflineUser(String recipientEmail, String message) {
        long pending = notifRepo.countByRecipient_EmailAndReadFalse(recipientEmail);

        emailSender.sendEmail(
            recipientEmail,
            mailFrom,
            "Hai una nuova notifica da It-Tickets",
            NotifyOfflineUserTemplateMessage.create(
                    templateEngine,
                    frontendUrl,
                    userRepo.findByEmail(recipientEmail).get(),
                    message,
                    pending
            )
        );
    }

    @Override
    public List<NotificationResponse> getPending(String recipient) {
        return notifRepo.findByRecipient_EmailAndReadFalseOrderByCreatedAtAsc(recipient)
                        .stream()
                        .map(notifMapper::notifToResponse)
                        .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getRead(String recipient) {
        return notifRepo.findByRecipient_EmailAndReadTrueOrderByCreatedAtAsc(recipient)
                .stream()
                .map(notifMapper::notifToResponse)
                .collect(Collectors.toList());
    }
}
