package it.degroup.it_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.degroup.it_tickets.entity.Role;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    List<Role> findByNameContaining(String fragment);

}