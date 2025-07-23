package it.degroup.it_tickets.providers.models;

import it.degroup.it_tickets.entity.User;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

public class EmailConfirmRegistrationTemplateMessage {

    public static String create(SpringTemplateEngine templateEngine, String frontendUrl, User user, String emailToken) {
        Context ctx = new Context();
        ctx.setVariable("name", user.getName());
        ctx.setVariable("confirmLink", frontendUrl + "/confirm_email?token=" + emailToken);
        String body = templateEngine.process("confirm-email", ctx);

        return body;
    }
}
