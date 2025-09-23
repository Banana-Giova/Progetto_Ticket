package it.degroup.it_tickets.common.exceptions;

import it.degroup.it_tickets.common.models.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "it.degroup.it_tickets")
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final HttpHeaders JSON_HEADERS;
    static {
        JSON_HEADERS = new HttpHeaders();
        JSON_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<OperationResult<?>> handleUnauthenticated(UnauthenticatedUserException ex) {
        log.warn("UnauthenticatedUserException: {}", ex.getMessage());
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .headers(JSON_HEADERS)
                .body(body);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<OperationResult<?>> handleDuplicate(UnauthorizedUserException ex) {
        log.warn("UnauthorizedUserException: {}", ex.getMessage());
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .headers(JSON_HEADERS)
                .body(body);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<OperationResult<?>> handleDuplicate(DuplicateException ex) {
        log.warn("DuplicateException: {}", ex.getMessage());
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .headers(JSON_HEADERS)
                .body(body);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<OperationResult<?>> handleDuplicate(DataAccessException ex) {
        log.warn("DataAccessException: {}", ex.getMessage());
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(JSON_HEADERS)
                .body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<OperationResult<?>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(JSON_HEADERS)
                .body(body);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<OperationResult<?>> handleUsernameNotFound(UsernameNotFoundException ex) {
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        log.warn("UsernameNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<OperationResult<?>> handleBadCredentials(BadCredentialsException ex) {
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        log.warn("BadCredentialsException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    // aggiungi anche il tuo EmailNotConfirmedException (se è custom) per restituire 401
    @ExceptionHandler(EmailNotConfirmedException.class)
    public ResponseEntity<OperationResult<?>> handleEmailNotConfirmed(EmailNotConfirmedException ex) {
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        log.warn("EmailNotConfirmedException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(org.springframework.security.authentication.ott.InvalidOneTimeTokenException.class)
    public ResponseEntity<OperationResult<?>> handleInvalidOneTimeToken(Exception ex) {
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        log.warn("InvalidOneTimeTokenException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<OperationResult<?>> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        OperationResult<?> body = OperationResult.ko(message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<OperationResult<?>> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException", ex);
        OperationResult<?> body = OperationResult.ko("Errore interno di runtime");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(JSON_HEADERS)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OperationResult<?>> handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        OperationResult<?> body = OperationResult.ko("Errore interno");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(JSON_HEADERS)
                .body(body);
    }
}
