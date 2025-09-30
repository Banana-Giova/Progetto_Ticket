package it.degroup.it_tickets.config;

import it.degroup.it_tickets.entity.Category;
import it.degroup.it_tickets.entity.Role;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.repository.CategoryRepository;
import it.degroup.it_tickets.repository.RoleRepository;
import it.degroup.it_tickets.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryRepository categoryRepo,
                           RoleRepository roleRepo,
                           UserRepository userRepo,
                           PasswordEncoder passwordEncoder) {
        this.categoryRepo = categoryRepo;
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // --- CATEGORIES ---
        List<String> cats = List.of(
                "Assistenza PC","Creazione Repository","VPN","Bolla PC","Assegnazione Dispositivi",
                "Installazione e Configurazione","Manutenzione Rete","Monitoraggio Prestazioni","Security",
                "Gestione Accessi","Backup e Ripristino Dati","Assistenza agli Utenti","Gestione Problemi Tecnici",
                "Aggiornamento Sistemi","Ottimizzazione Risorse","Altro"
        );

        for (String c : cats) {
            categoryRepo.findByName(c)
                    .orElseGet(() -> categoryRepo.save(new Category(null, c)));
        }

        // --- ROLES ---
        List<String> roles = List.of("Utente","Operatore","Amministratore");
        for (String r : roles) {
            roleRepo.findByName(r).orElseGet(() -> roleRepo.save(new Role(r)));
        }

        // --- ADMIN USER ---
        String adminEmail = "test@test.com";
        if (userRepo.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setName("Admin");
            admin.setSurname("Admin");

            // ********* Sostituire la password *********
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setEmailConfirmed(true);
            admin.setRoles(new HashSet<>());

            roleRepo.findByName("Utente").ifPresent(admin.getRoles()::add);
            roleRepo.findByName("Operatore").ifPresent(admin.getRoles()::add);
            roleRepo.findByName("Amministratore").ifPresent(admin.getRoles()::add);

            try {
                userRepo.save(admin);
            } catch (DataIntegrityViolationException ex) {
                User existing = userRepo.findByEmail(adminEmail).orElseThrow();
                roleRepo.findByName("Utente").ifPresent(existing.getRoles()::add);
                roleRepo.findByName("Operatore").ifPresent(existing.getRoles()::add);
                roleRepo.findByName("Amministratore").ifPresent(existing.getRoles()::add);
                userRepo.save(existing);
            }
        }
    }
}
