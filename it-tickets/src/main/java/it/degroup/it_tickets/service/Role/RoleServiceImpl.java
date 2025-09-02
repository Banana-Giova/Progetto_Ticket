package it.degroup.it_tickets.service.Role;

import it.degroup.it_tickets.common.mappers.RoleMapper;
import it.degroup.it_tickets.common.mappers.UserMapper;
import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.responses.RoleResponse;
import it.degroup.it_tickets.repository.RoleRepository;
import it.degroup.it_tickets.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    @Autowired
    private RoleMapper roleMapper;

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
    public void assignRoleToUser(String email, String roleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non esistente: " + email));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public List<User> getUsersByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non esistente: " + roleName));
        return List.copyOf(role.getUsers());
    }
}
