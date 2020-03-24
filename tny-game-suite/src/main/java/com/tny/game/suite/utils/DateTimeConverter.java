package com.tny.game.suite.utils;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter extends AbstractSingleValueConverter {

    private DateTimeFormatter format;

    public DateTimeConverter(DateTimeFormatter format) {
        this.format = format;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean canConvert(Class clazz) {
        return clazz.isAssignableFrom(Instant.class);
    }

    @Override
    public Object fromString(String value) {
        return Instant.from(this.format.parse(value));
    }
}
