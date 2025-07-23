package it.degroup.it_tickets.common.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderImpl implements EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String from, String subject, String body) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
        };
        // MIME = Multipurpose Internet Mail Extensions -> Standard più avanzato di SMTP
        try {
            mailSender.send(preparator);
        } catch (MailException e) {
            throw new RuntimeException("Errore durante l'invio dell'email: ", e);
        }
    }
}
