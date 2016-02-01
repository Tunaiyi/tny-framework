package com.tny.game.web.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpUtils {

    public static ResponseEntity<String> status(HttpStatus status) {
        return new ResponseEntity<String>(status);
    }

    public static ResponseEntity<String> status(String message, HttpStatus status) {
        return new ResponseEntity<String>(message, status);
    }

}
