package it.degroup.it_tickets.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.degroup.it_tickets.service.MyUserDetailService;
import it.degroup.it_tickets.service.User.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER_KEY = "Authorization";
    private static final String REFRESH_HEADER_KEY = "X-Refresh-Token";
    private static final String TOKEN_TYPE_HEADER_KEY = "Bearer";

    private final UserService userserivce;
    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper;
    private final MyUserDetailService userdetail;

    // Inietti il servizio nel costruttore
    public JwtAuthorizationFilter(UserService userserivce, JwtUtil jwtUtil, ObjectMapper mapper, MyUserDetailService userdetail) {
        this.userserivce = userserivce;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.userdetail = userdetail;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> errorDetails = new HashMap<>();

        try {
            String authHeader = request.getHeader(AUTH_HEADER_KEY) != null ? request.getHeader(AUTH_HEADER_KEY) : request.getHeader(REFRESH_HEADER_KEY);
            if ((request.getRequestURI().contains("login")
                    || request.getRequestURI().contains("register"))
                    || request.getRequestURI().contains("email-confirmation")
                    || request.getRequestURI().contains("forgot-password")
                    && authHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }
            if(authHeader != null){
                String token = jwtUtil.resolveToken(request);
                String email = jwtUtil.getEmailFromToken(token);
                UserDetails user = userdetail.loadUserByUsername(email);
                if(user == null) throw new Exception("utente non autenticato");

                if (jwtUtil.isTokenExpired(token))  this.refreshToken(request,response);
                else {
                    Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    response.setHeader(AUTH_HEADER_KEY, TOKEN_TYPE_HEADER_KEY + token);
                }
                filterChain.doFilter(request, response);
            }
        }catch (Exception e){
            errorDetails.put("message", "Authentication Error");
            errorDetails.put("details",e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            mapper.writeValue(response.getWriter(), errorDetails);

        }
    }

    private String refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refreshToken=request.getHeader(REFRESH_HEADER_KEY);
        if (refreshToken== null || refreshToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new Exception("Refresh token non presente");
        }
        String email = jwtUtil.getEmailFromToken(refreshToken);
        String token = jwtUtil.generateToken(email);
        response.setHeader(REFRESH_HEADER_KEY, TOKEN_TYPE_HEADER_KEY + "" + token);
        return token;
    }
    
}