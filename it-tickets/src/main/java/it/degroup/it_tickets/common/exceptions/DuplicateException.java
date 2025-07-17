package it.degroup.it_tickets.common.exceptions;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String email) {
        super("Email already in use: " + email);
    }
}