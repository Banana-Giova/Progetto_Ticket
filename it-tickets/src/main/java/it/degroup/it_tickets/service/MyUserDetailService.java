package it.degroup.it_tickets.service;

import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.repository.UserRepository;
import it.degroup.it_tickets.service.User.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        System.out.println("User id: " + user.get().getId());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with username: " + email);
        }
        return new MyUserDetails(user.get());
    }
}
