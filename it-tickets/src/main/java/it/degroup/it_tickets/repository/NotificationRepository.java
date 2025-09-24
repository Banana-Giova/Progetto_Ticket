package it.degroup.it_tickets.repository;

import it.degroup.it_tickets.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>  {
    List<Notification> findByRecipient_EmailAndReadFalseOrderByCreatedAtAsc(String recipientEmail);
    long countByRecipient_EmailAndReadFalse(String recipientEmail);
}
