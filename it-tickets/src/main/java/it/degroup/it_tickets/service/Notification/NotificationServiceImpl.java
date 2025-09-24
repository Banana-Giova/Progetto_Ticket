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
        Optional<User> curr_user = userRepo.findByEmail(recipientEmail);

        Notification newNotif = Notification.builder()
                .recipient(curr_user.get())
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        newNotif = notifRepo.save(newNotif);
        NotificationResponse notifResp = notifMapper.notifToResponse(newNotif);

        if (!curr_user.isEmpty() &&
                curr_user.get().getToken().isEmpty()) {
            this.emailOfflineUser(recipientEmail, message);
            return notifResp;
        }

        messagingTemplate.convertAndSend(String.format("%s/queue/notifications", curr_user.get().getEmail()),
                                         notifResp);
        return notifResp;
    }

    @Override
    public void notifyAllOperators(String message) {
        List<User> operators = userRepo.findOnlyOperatorsList();

        for (User operator : operators) {
            Notification newNotif = Notification.builder()
                    .recipient(operator)
                    .message(message)
                    .read(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            newNotif = notifRepo.save(newNotif);
            NotificationResponse notifResp = notifMapper.notifToResponse(newNotif);

            if (operator.getToken().isEmpty()) {
                emailOfflineUser(operator.getEmail(), message);
            } else {
                messagingTemplate.convertAndSend(String.format("%s/queue/notifications", operator.getEmail()),
                        notifResp);
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
}
