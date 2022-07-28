package com.coindirect.recruitment;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AlreadyBookedException extends ResponseStatusException {

    public AlreadyBookedException() {
        super(HttpStatus.OK, "The position is already booked");
    }

}
