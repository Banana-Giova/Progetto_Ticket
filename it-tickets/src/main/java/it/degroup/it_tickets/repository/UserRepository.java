package it.degroup.it_tickets.repository;

import it.degroup.it_tickets.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailToken(String token);

    boolean existsByEmail(String email);

    List<User> findAllByEmailConfirmedTrue();

    List<User> findAllByRoles_Name(String roleName);


}