package it.degroup.it_tickets.common.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHelper {
    private final PasswordEncoder encoder;

    public PasswordHelper(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public boolean matches(String raw, String encoded) {
        return encoder.matches(raw, encoded);
    }

    public String encode(String raw) {
        return encoder.encode(raw);
    }
}
