package it.degroup.it_tickets.providers.models;

import it.degroup.it_tickets.entity.User;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

public class ForgotPasswordEmailTemplateMessage {

    public static String create(
            SpringTemplateEngine templateEngine,
            String frontendUrl,
            User user,
            String userMail,
            String tempPassword,
            String passwordToken
    ) {
        Context ctx = new Context();
        ctx.setVariable("name", user.getName());
        ctx.setVariable("tempPassword", tempPassword);
        ctx.setVariable("resetLink", frontendUrl + "/reset-password?passToken=" + passwordToken);
        String body = templateEngine.process("forgot-password", ctx);

        return body;
    }
}
