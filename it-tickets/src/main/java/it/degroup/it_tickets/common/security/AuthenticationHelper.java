package it.degroup.it_tickets.common.security;

import it.degroup.it_tickets.common.exceptions.UnauthorizedUserException;
import it.degroup.it_tickets.common.exceptions.UnauthenticatedUserException; // nuovo checked
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.service.User.MyUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationHelper {

     // Ritorna i MyUserDetails dell'utente autenticato.
    // Lancia UnauthenticatedUserException se non è presente un utente autenticato.
    public MyUserDetails currentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            log.warn("Nessuna autenticazione presente nel SecurityContext");
            throw new UnauthenticatedUserException("Utente non autenticato.");
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof MyUserDetails)) {
            log.warn("Principal non è MyUserDetails: {}", principal == null ? "null" : principal.getClass().getName());
            throw new UnauthenticatedUserException("Utente non autenticato correttamente.");
        }

        return (MyUserDetails) principal;
    }

    public User getUser() throws UnauthenticatedUserException {
        return this.currentUserDetails().getUser();
    }

    // Ritorna l'utente se è operatore, altrimenti lancia UnauthorizedUserException; simile per il metodo sotto questo.
    public User getOperatorOrError() {
        User currUser = this.currentUserDetails().getUser();
        if (currUser.isOperator()) {
            return currUser;
        } else {
            log.warn("Accesso operatori negato per l'utente id={}", currUser.getId());
            throw new UnauthorizedUserException("Area operatori, utente non autorizzato.");
        }
    }

    public User getAdminOrError() {
        User currUser = this.currentUserDetails().getUser();
        if (currUser.isAdmin()) {
            return currUser;
        } else {
            log.warn("Accesso admin negato per l'utente id={}", currUser.getId());
            throw new UnauthorizedUserException("Area amministratori, utente non autorizzato.");
        }
    }
}
