package fr.geckocode.stepbystep.exceptions;

public class EmailDupplicationException extends RuntimeException {
    public EmailDupplicationException(String message) {
        super(message);
    }
}
