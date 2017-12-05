package com.tny.game.suite.base.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tny.game.common.enums.EnumID;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class EnumIDJsonSerializer<E extends Enum<E> & EnumID<ID>, ID> extends JsonSerializer<E> {

    @Override
    public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeObject(value.getID());
    }

}
