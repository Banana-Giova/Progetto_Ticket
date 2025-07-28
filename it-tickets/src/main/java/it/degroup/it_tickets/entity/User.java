package it.degroup.it_tickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    }
}