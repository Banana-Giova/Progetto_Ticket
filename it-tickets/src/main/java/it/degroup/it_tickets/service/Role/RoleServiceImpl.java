package it.degroup.it_tickets.service.Role;

import it.degroup.it_tickets.common.exceptions.UnauthorizedUserException;
import it.degroup.it_tickets.common.mappers.RoleMapper;
import it.degroup.it_tickets.common.mappers.UserMapper;
import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.RoleResponseWOUsers;
import it.degroup.it_tickets.presentation.responses.UserResponseWithRoles;
import it.degroup.it_tickets.presentation.responses.RoleResponseWithUsers;
import it.degroup.it_tickets.repository.RoleRepository;
import it.degroup.it_tickets.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;

    public RoleServiceImpl(RoleRepository roleRepository,
                           UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RoleResponseWithUsers createRole(String roleName) {
        if (roleRepository.existsByName(roleName))
            throw new IllegalArgumentException("Ruolo già esistente: " + roleName);
        Role new_role = roleRepository.save(new Role(roleName));
        return roleMapper.roleToRoleResponseWithUsers(new_role);
    }

    @Override
    public RoleResponseWithUsers getRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));
        return roleMapper.roleToRoleResponseWithUsers(role);
    }

    @Override
    public List<RoleResponseWOUsers> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .sorted(Comparator.comparing(Role::getId))
                .map(roleMapper::roleToRoleResponseWOUsers)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseWithRoles> getUsersByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));

        if (role.getUsers() == null || role.getUsers().isEmpty()) {
            return List.of();
        }

        return role.getUsers()
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .map(userMapper::userToUserResponseWithRoles)
                .collect(Collectors.toList());
    }

    @Override
    public void assignRoleToUser(String email, String roleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non esistente: " + email));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));

        List<String> allowedRoles = List.of("Utente", "Operatore", "Amministratore");
        if (!allowedRoles.contains(roleName))
            throw new IllegalArgumentException("Ruolo non ammesso: " + roleName);

        boolean alreadyAssigned = user.getRoles() != null &&
                user.getRoles().stream().anyMatch(r -> roleName.equals(r.getName()));
        if (alreadyAssigned)
            throw new IllegalArgumentException(roleName + " già assegnato a " + email);

        if (user.getRoles() == null)
            user.setRoles(new HashSet<>());
        if (role.getUsers() == null)
            role.setUsers(new HashSet<>());

        user.getRoles().add(role);
        role.getUsers().add(user);

        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(String email, String roleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non esistente: " + email));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));

        if ("Utente".equals(roleName))
            throw new IllegalStateException("Il ruolo 'Utente' non può essere rimosso.");
        if ("Amministratore".equals(roleName))
            throw new UnauthorizedUserException("Il ruolo 'Amministratore' non può essere rimosso.");

        boolean hasRole = user.getRoles() != null &&
                user.getRoles().stream().anyMatch(r -> roleName.equals(r.getName()));
        if (!hasRole)
            throw new IllegalStateException("Utente non ha il ruolo: " + roleName);

        user.getRoles().removeIf(r -> roleName.equals(r.getName()));
        if (role.getUsers() != null)
            role.getUsers().remove(user);

        userRepository.save(user);
    }
}