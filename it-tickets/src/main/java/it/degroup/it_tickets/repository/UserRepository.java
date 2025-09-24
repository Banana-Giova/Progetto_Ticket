package it.degroup.it_tickets.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import it.degroup.it_tickets.entity.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailToken(String token);

    Optional<User> findByPasswordToken(String token);

    boolean existsByEmail(String email);

    List<User> findAllByEmailConfirmedTrue();

    List<User> findAllByRoles_Name(String roleName);

    @Query("SELECT u.token FROM User u WHERE u.email = :email")
    Optional<String> getTokenByEmail(@Param("email") String email);

    // filtro keyword su name OR surname OR email
    Page<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String surname, String email, Pageable pageable);

    // Filtro per Utenti (solo ruolo Utente)
    @Query("SELECT u FROM User u WHERE u IN (SELECT u2 FROM User u2 JOIN u2.roles r WHERE r.name = 'Utente') AND u NOT IN (SELECT u3 FROM User u3 JOIN u3.roles r WHERE r.name IN ('Operatore', 'Amministratore'))")
    Page<User> findOnlyUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE (u.name LIKE %:keyword% OR u.surname LIKE %:keyword% OR u.email LIKE %:keyword%) AND u IN (SELECT u2 FROM User u2 JOIN u2.roles r WHERE r.name = 'Utente') AND u NOT IN (SELECT u3 FROM User u3 JOIN u3.roles r WHERE r.name IN ('Operatore', 'Amministratore'))")
    Page<User> findOnlyUsersByKeyword(String keyword, Pageable pageable);

    // Filtro per Operatori (ruoli Utente + Operatore, ma NON Amministratore)
    @Query("SELECT u FROM User u WHERE u IN (SELECT u2 FROM User u2 JOIN u2.roles r WHERE r.name = 'Operatore') AND u NOT IN (SELECT u3 FROM User u3 JOIN u3.roles r WHERE r.name = 'Amministratore')")
    Page<User> findOnlyOperators(Pageable pageable);

    @Query("SELECT u FROM User u WHERE (u.name LIKE %:keyword% OR u.surname LIKE %:keyword% OR u.email LIKE %:keyword%) AND u IN (SELECT u2 FROM User u2 JOIN u2.roles r WHERE r.name = 'Operatore') AND u NOT IN (SELECT u3 FROM User u3 JOIN u3.roles r WHERE r.name = 'Amministratore')")
    Page<User> findOnlyOperatorsByKeyword(String keyword, Pageable pageable);

    // Filtro per Amministratori (già isolati)
    Page<User> findByRolesNameIgnoreCase(String roleName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (u.name LIKE %:keyword% OR u.surname LIKE %:keyword% OR u.email LIKE %:keyword%) AND u IN (SELECT u2 FROM User u2 JOIN u2.roles r WHERE r.name = :roleName)")
    Page<User> findByRoleNameAndKeyword(String roleName, String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u IN (SELECT u2 FROM User u2 JOIN u2.roles r WHERE r.name = 'Operatore') AND u NOT IN (SELECT u3 FROM User u3 JOIN u3.roles r WHERE r.name = 'Amministratore')")
    List<User> findOnlyOperatorsList();
}