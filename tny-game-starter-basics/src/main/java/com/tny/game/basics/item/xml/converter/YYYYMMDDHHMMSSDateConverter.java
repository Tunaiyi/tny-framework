package com.tny.game.basics.item.xml.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.text.*;
import java.util.Date;

public class YYYYMMDDHHMMSSDateConverter extends AbstractSingleValueConverter {

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @SuppressWarnings({"unchecked"})
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
