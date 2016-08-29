package com.tny.game.web.utils;

import com.tny.game.common.result.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    public static ResponseEntity<String> status(HttpStatus status) {
        return new ResponseEntity<>(status);
    }

    public static ResponseEntity<String> status(String message, HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }

    public static ResponseEntity<String> status(ResultCode code, HttpStatus status) {
        return new ResponseEntity<>(code.getMessage(), status);
    }

    public static ResponseEntity<String> serverError(ResultCode code) {
        return new ResponseEntity<>(code.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public static Map<String, String> requestParam2Map(HttpServletRequest request) {
        return requestParam2Map(request, new HashMap<String, String>());
    }

    public static <M extends Map<String, String>> M requestParam2Map(HttpServletRequest request, M map) {
        for (String key : request.getParameterMap().keySet()) {
            String value = request.getParameter(key);
            map.put(key, value);
        }
        return map;
    }

}
