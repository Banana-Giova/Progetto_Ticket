package it.degroup.it_tickets.common.exceptions;

public class EmailAlreadyConfirmedException extends RuntimeException {
    public EmailAlreadyConfirmedException(String message) {
        super(message);
    }
}
