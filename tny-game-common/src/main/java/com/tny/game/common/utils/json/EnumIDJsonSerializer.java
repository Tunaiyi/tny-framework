package com.tny.game.common.utils.json;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.common.enums.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class EnumIDJsonSerializer<E extends Enum<E> & EnumIdentifiable<ID>, ID> extends JsonSerializer<E> {

    @Override
    public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeObject(value.getId());
    }

}
