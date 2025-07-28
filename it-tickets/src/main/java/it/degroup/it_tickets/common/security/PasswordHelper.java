package it.degroup.it_tickets.common.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordHelper {
    private final PasswordEncoder encoder;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+";
    private static final String ALL = LOWER + UPPER + DIGITS + SYMBOLS;

    public PasswordHelper(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    private static final Pattern VALIDATOR = Pattern.compile(
            "^(?=.*[0-9])" +          // almeno una cifra
                    "(?=.*[!@#$%^&*])" +      // almeno un simbolo
                    "[a-zA-Z0-9!@#$%^&*]{8,24}$"
    );

    public static String generateTemporaryPassword(int length) {
        if (length < 8 || length > 24) {
            throw new IllegalArgumentException("Lunghezza password deve essere tra 8 e 24");
        }

        String pwd;
        do {
            // 1) Assicura un carattere di ogni categoria
            List<Character> chars = new ArrayList<>(length);
            chars.add(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
            chars.add(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
            chars.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
            chars.add(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
            // 2) Riempi il resto con caratteri casuali
            for (int i = 4; i < length; i++) {
                chars.add(ALL.charAt(RANDOM.nextInt(ALL.length())));
            }
            // 3) Mischia per spargere le categorie
            Collections.shuffle(chars, RANDOM);
            // 4) Ricomponi la stringa
            StringBuilder sb = new StringBuilder(length);
            for (char c : chars) sb.append(c);
            pwd = sb.toString();
            // 5) Verifica il regex
        } while (!VALIDATOR.matcher(pwd).matches());

        return pwd;
    }

    public boolean matches(String raw, String encoded) {
        return encoder.matches(raw, encoded);
    }

    public String encode(String raw) {
        return encoder.encode(raw);
    }
}
