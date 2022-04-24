package com.tny.game.basics.persistent.annotation;

public enum Modify {

    INSERT("INSERT"),

    UPDATE("UPDATE"),

    SAVE("SAVE"),

    DELETE("DELETE"),

    //
    ;

    String value;

    Modify(String value) {
        this.value = value;
    }
}
