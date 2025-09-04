package it.degroup.it_tickets.service.Role;

import it.degroup.it_tickets.common.mappers.RoleMapper;
import it.degroup.it_tickets.common.mappers.UserMapper;
import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.ProfileResponse;
import it.degroup.it_tickets.presentation.responses.RoleResponse;
import it.degroup.it_tickets.repository.RoleRepository;
import it.degroup.it_tickets.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
    public RoleResponse createRole(String roleName) {
        if (roleRepository.existsByName(roleName))
            throw new IllegalArgumentException("Ruolo già esistente: " + roleName);
        Role new_role = roleRepository.save(new Role(roleName));
        return roleMapper.roleToRoleResponse(new_role);
    }

    @Override
    public RoleResponse getRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));
        return roleMapper.roleToRoleResponse(role);
    }

    @Override
    public List<ProfileResponse> getUsersByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));

        if (role.getUsers() == null || role.getUsers().isEmpty()) {
            return List.of();
        }

        return role.getUsers()
                   .stream()
                   .sorted(Comparator.comparing(User::getId))
                   .map(userMapper::userToProfileResponse)
                   .collect(Collectors.toList());
    }

    @Override
    public void assignRoleToUser(String email, String roleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non esistente: " + email));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));

        if (!user.getRoles().isEmpty()) {
            boolean alreadyAssigned = user.getRoles()
                    .stream()
                    .anyMatch(r -> roleName.equals(r.getName()));
            if (alreadyAssigned)
                throw new IllegalArgumentException("Ruolo " + roleName + " già assegnato all'utente con email " + email);
        }
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(String email, String roleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non esistente: " + email));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));

        if (!user.getRoles().contains(role)) {
            throw new IllegalStateException("Utente non ha il ruolo: " + roleName);
        }

        user.getRoles().remove(role);
        role.getUsers().remove(user);

        userRepository.save(user);
    }

}
