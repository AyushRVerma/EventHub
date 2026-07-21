package com.eventbook.EventHub.exceptions;

public class InvalidEventDatesException extends EventTicketException{

    public InvalidEventDatesException() {
    }

    public InvalidEventDatesException(String message) {
        super(message);
    }

    public InvalidEventDatesException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEventDatesException(Throwable cause) {
        super(cause);
    }

    public InvalidEventDatesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
