package com.tny.game.suite.base.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class YYYYMMDDHHMMSSDateConverter extends AbstractSingleValueConverter {

    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean canConvert(Class clazz) {
        return clazz.isAssignableFrom(Date.class);
    }

    @Override
    public Object fromString(String value) {
        try {
            return this.format.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
