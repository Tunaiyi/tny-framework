package com.tny.game.common.io.config;

public interface ConfigFormatter {

    boolean isKey(String key);

    Object formatObject(String value);

}
