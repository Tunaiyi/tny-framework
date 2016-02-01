package com.tny.game.common.config;

public interface ConfigFormatter {

    public boolean isKey(String key);

    public Object formatObject(String value);

}
