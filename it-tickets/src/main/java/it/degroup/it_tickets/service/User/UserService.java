package it.degroup.it_tickets.service.User;

import it.degroup.it_tickets.presentation.requests.*;
import it.degroup.it_tickets.entity.User;
import it.degroup.it_tickets.presentation.requests.LoginRequest;
import it.degroup.it_tickets.presentation.requests.RegisterRequest;
import it.degroup.it_tickets.presentation.requests.ResetPasswordRequest;
import it.degroup.it_tickets.presentation.responses.LoginResponse;
import it.degroup.it_tickets.presentation.responses.UserResponseWithRoles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import it.degroup.it_tickets.presentation.responses.PagingResult;

import java.util.Optional;

public interface UserService {

    UserResponseWithRoles register(RegisterRequest dto);
    void sendEmailToken(String user_email);
    void confirmEmailToken(EmailTokenRequest email_token);
    LoginResponse authenticate(LoginRequest request);
    void logout(OnlyEmailRequest request);
    Optional<String> getTokenByEmail(String email);
    Optional<User> findUserByEmail(String email);
    void resetPassword(ResetPasswordRequest request);
    void forgotPassword(OnlyEmailRequest request);
    Page<UserResponseWithRoles> getUsersListWithFilters(
            String keyword,
            String roleName,
            Pageable pageable
    );
    UserResponseWithRoles profileFetch(User user);
}
