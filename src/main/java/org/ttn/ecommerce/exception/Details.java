package org.ttn.ecommerce.exception;

import java.time.LocalDateTime;


public class Details {
    private LocalDateTime timeStamp;
    private String message;
    private String details;

    public Details(LocalDateTime timeStamp, String message, String details) {
        super();
        this.timeStamp = timeStamp;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}