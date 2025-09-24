package it.degroup.it_tickets.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class StompDebugListener {

    @EventListener
    public void onSessionConnected(SessionConnectEvent ev) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(ev.getMessage());
        log.info("STOMP CONNECT from user={} sessionId={}", sha.getUser(), sha.getSessionId());
    }

    @EventListener
    public void onSessionSubscribe(SessionSubscribeEvent ev) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(ev.getMessage());
        String dest = sha.getDestination();
        log.info("STOMP SUBSCRIBE from user={} sessionId={} dest={}", sha.getUser(), sha.getSessionId(), dest);
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent ev) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(ev.getMessage());
        log.info("STOMP DISCONNECT sessionId={} closeStatus={}", sha.getSessionId(), ev.getCloseStatus());
    }
}
