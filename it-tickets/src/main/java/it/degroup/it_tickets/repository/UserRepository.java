package it.degroup.it_tickets.repository;

import it.degroup.it_tickets.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional< it.degroup.it_tickets.entity.User> findByEmail(String email);
    boolean existsByEmail(String email);
}
