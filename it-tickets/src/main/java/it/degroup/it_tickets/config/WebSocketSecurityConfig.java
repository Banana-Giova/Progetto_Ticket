/*
package it.degroup.it_tickets.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.messaging.Message;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    // Permetti tutto: solo per testing / debug
    @Bean
    public AuthorizationManager<Message<?>> webSocketAuthorizationManager() {
        return (authentication, message) -> new AuthorizationDecision(true);
    }
}
*/