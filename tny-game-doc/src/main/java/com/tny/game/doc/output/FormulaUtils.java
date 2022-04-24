package com.tny.game.doc.output;

import java.net.*;

public class FormulaUtils {

    public static String ufl(String value) {
        if (value.length() == 0) {
            return value;
        }
        if (value.length() == 1) {
            return value.toUpperCase();
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static String lfl(String value) {
        if (value.length() == 0) {
            return value;
        }
        if (value.length() == 1) {
            return value.toLowerCase();
        }
        return value.substring(0, 1).toLowerCase() + value.substring(1);
    }

    public static String ul(String value) {
        return value.toUpperCase();
    }

    public static String ll(String value) {
        return value.toLowerCase();
    }

    public static String urle(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String urld(String url) {
        try {
            return URLDecoder.decode(url, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
