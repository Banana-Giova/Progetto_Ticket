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

    // filtro per ruolo (join implicito su roles)
    Page<User> findByRolesNameIgnoreCase(String roleName, Pageable pageable);

    // filtro keyword su name OR surname OR email
    Page<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String surname, String email, Pageable pageable);

    // combinata: ruolo + keyword (una sola query JPQL, più leggibile e performante che creare tutte le combinazioni derive)
    @Query("""
        select distinct u
        from User u
        join u.roles r
        where lower(r.name) = lower(:roleName)
          and (
            lower(u.name) like lower(concat('%', :keyword, '%')) 
            or lower(u.surname) like lower(concat('%', :keyword, '%'))
            or lower(u.email) like lower(concat('%', :keyword, '%'))
          )
    """)
    Page<User> findByRoleNameAndKeyword(@Param("roleName") String roleName,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);
}