package it.degroup.it_tickets.providers.models;

import it.degroup.it_tickets.entity.User;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

public class NotifyOfflineUserTemplateMessage {

    public static String create(
            SpringTemplateEngine templateEngine,
            String frontendUrl,
            User user,
            String notificationMessage,
            long pendingCount
    ) {
        Context ctx = new Context();
        ctx.setVariable("name", user.getName() != null ? user.getName() : user.getEmail());
        ctx.setVariable("message", notificationMessage);
        ctx.setVariable("pendingCount", pendingCount);
        ctx.setVariable("notificationsLink", frontendUrl + "/notifications");
        ctx.setVariable("viewLink", frontendUrl + "/notifications"); // alias
        String body = templateEngine.process("notify-offline", ctx);
        return body;
    }

    public static String subject(long pendingCount) {
        if (pendingCount <= 1) {
            return "Hai 1 nuova notifica su It-Tickets";
        } else {
            return "Hai " + pendingCount + " nuove notifiche su It-Tickets";
        }
    }
}