package it.degroup.it_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.degroup.it_tickets.entity.User;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByEmailConfirmedTrue();

    List<User> findAllByRoles_Name(String roleName);


}