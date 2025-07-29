package it.degroup.it_tickets.service;

import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.repository.UserRepository;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with username: " + email);
        }
        return new org.springframework.security.core.userdetails.User( //restituisce un oggetto User di Spring Security, che implementa l'interfaccia UserDetails.
                user.get().getEmail(),  //è lo username da autenticare
                user.get().getPassword(), // è la password dall'utente che viene confrontata con quella ricevuta dall'utente
                user.get().getEmailConfirmed(),
                true,
                true,
                true,
                Collections.emptyList() //rappresenta la lista vuota di ruoli/ permessi
        );
    }
}
