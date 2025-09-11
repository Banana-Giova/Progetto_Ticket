package it.degroup.it_tickets.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String token;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String password;
    @Column(name = "email_confirmed", nullable = false)
    private Boolean emailConfirmed;
    @Column(name = "password_token", unique = true)
    private String passwordToken;
    @Column(name = "email_token", unique = true)
    private String emailToken;
    @Column(name = "password_expiration")
    private LocalDateTime passwordExpiration;
    @Column(name = "email_expiration")
    private LocalDateTime emailExpiration;

    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    public User(String email, String name, String surname, String password) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.emailConfirmed = false;
        this.passwordToken = null;
        this.emailToken = null;
        this.passwordExpiration = null;
        this.emailExpiration = null;
        this.token = null;
        this.roles = new HashSet<>();
    }

    public List<String> getRoleList() {
        Set<Role> roleSet = this.getRoles();
        if (roleSet == null || roleSet.isEmpty()) {
            return List.of();
        }

        List<String> roleList = new ArrayList<>();
        for (Role role : new ArrayList<>(roleSet)) {
            roleList.add(role.getName());
        }
        return roleList;
    }

    public boolean isOperator() {
        if (this.getRoleList().contains("Operatore")) {
            return true;
        } return false;
    }

    public boolean isAdmin() {
        if (this.getRoleList().contains("Amministratore")) {
            return true;
        } return false;
    }

    public boolean isEmailTokenExpired() {
        if (this.getEmailExpiration().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Email token scaduto! Si è pregati di registrare di nuovo l'account");
        return this.getEmailExpiration().isBefore(LocalDateTime.now());
    }

    public boolean isPasswordTokenNotExpired() {
        if (this.getPasswordExpiration() == null || !this.getPasswordExpiration().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Password token scaduto! Si è pregati di rifare Password Dimenticata.");
        }
        return true;
    }

    public boolean isPasswordTokenValid(String token) {
        String currToken = this.getPasswordToken();
        if (currToken == null || token == null)
            throw new BadCredentialsException("Password token invalido! Si è pregati di rifare Password Dimenticata.");
        if (!currToken.equals(token))
            throw new BadCredentialsException("Password token invalido! Si è pregati di rifare Password Dimenticata.");

        return true;
    }

    public boolean passwordTokenCheck(String token) {
        if (this.passwordToken == null) {
            throw new InvalidOneTimeTokenException("Nessun token associato all’utente!");
        }
        return isPasswordTokenNotExpired() && isPasswordTokenValid(token);
    }
}