package it.degroup.it_tickets.common.exceptions;

import it.degroup.it_tickets.common.models.OperationResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final HttpHeaders JSON_HEADERS;
    static {
        JSON_HEADERS = new HttpHeaders();
        JSON_HEADERS.setContentType(MediaType.APPLICATION_JSON);
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<OperationResult<?>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        OperationResult<?> body = OperationResult.ko(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(JSON_HEADERS)
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
