package it.degroup.it_tickets.service.Notification;

import it.degroup.it_tickets.presentation.responses.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse notifyUser(String recipient, String message);

    void notifyAllOperators(String message);

    NotificationResponse markasRead(Long id);

    void emailOfflineUser(String recipient, String message);

    List<NotificationResponse> getPending(String recipient);

    List<NotificationResponse> getRead(String recipient);
}
