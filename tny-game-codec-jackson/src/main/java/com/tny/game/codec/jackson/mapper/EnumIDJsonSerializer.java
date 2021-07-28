package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.tny.game.common.enums.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class EnumIDJsonSerializer<E extends Enum<E> & EnumIdentifiable<ID>, ID> extends JsonSerializer<E> {

    public EnumIDJsonSerializer() {
    }

    @Override
    public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeObject(null);
        } else {
            gen.writeObject(((EnumIdentifiable<?>)value).getId());
        }

    }

}
