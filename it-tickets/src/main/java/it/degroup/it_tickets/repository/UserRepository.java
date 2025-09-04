package it.degroup.it_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.degroup.it_tickets.entity.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailToken(String token);

    boolean existsByEmail(String email);

    List<User> findAllByEmailConfirmedTrue();

    List<User> findAllByRoles_Name(String roleName);

    @Query("SELECT u.token FROM User u WHERE u.email = :email")
    Optional<String> getTokenByEmail(@Param("email") String email);

}