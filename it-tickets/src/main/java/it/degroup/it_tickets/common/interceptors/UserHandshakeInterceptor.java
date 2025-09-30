package it.degroup.it_tickets.common.interceptors;

import it.degroup.it_tickets.common.models.StompPrincipal;
import it.degroup.it_tickets.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletReq = ((ServletServerHttpRequest) request).getServletRequest();
            String token = servletReq.getParameter("access_token");
            if (token != null) {
                // se viene prefissato con “Bearer ” rimuovilo
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                String email = jwtUtil.extractEmail(token);
                if (email != null) {
                    attributes.put("principal", new StompPrincipal(email));
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
    }
}
