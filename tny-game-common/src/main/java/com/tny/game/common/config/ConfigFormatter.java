package com.tny.game.common.config;

public interface ConfigFormatter {

    boolean isKey(String key);

    Object formatObject(String value);

}
