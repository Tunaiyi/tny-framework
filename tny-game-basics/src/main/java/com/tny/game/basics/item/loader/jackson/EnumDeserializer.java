package com.tny.game.basics.item.loader.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 04:43
 **/
public class EnumDeserializer<T> extends JsonDeserializer<T> {

    private final EnumMapper<T> enumMapper;

    public EnumDeserializer(EnumMapper<T> enumMapper) {
        this.enumMapper = enumMapper;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        T enumObject = this.enumMapper.getEnum(value);
        if (enumObject == null) {
            throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", value));
        }
        return enumObject;
    }

}
