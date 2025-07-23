package it.degroup.it_tickets.common.providers;

public interface EmailSender {

    void sendEmail(String to, String from, String subject,  String body);
}
