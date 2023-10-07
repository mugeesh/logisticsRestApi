package com.logistics.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MapException extends RuntimeException {

    public MapException(String msg) {
        super(msg);
    }
}
