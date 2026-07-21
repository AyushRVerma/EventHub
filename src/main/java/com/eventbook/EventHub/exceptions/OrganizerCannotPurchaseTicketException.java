package com.eventbook.EventHub.exceptions;

public class OrganizerCannotPurchaseTicketException extends RuntimeException {
    public OrganizerCannotPurchaseTicketException() {
    }

    public OrganizerCannotPurchaseTicketException(String message) {
        super(message);
    }

    public OrganizerCannotPurchaseTicketException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrganizerCannotPurchaseTicketException(Throwable cause) {
        super(cause);
    }

    public OrganizerCannotPurchaseTicketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
