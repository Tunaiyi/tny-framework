package com.tny.game.common.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.tny.game.common.enums.EnumIdentifiable;
import com.tny.game.common.enums.EnumAide;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class EnumIDJsonDeserializer<E extends Enum<E> & EnumIdentifiable<ID>, ID> extends JsonDeserializer<E> {

    private Class<E> enumClass;
    private Class<ID> idClass;

    public EnumIDJsonDeserializer(Class<E> enumClass, Class<ID> idClass) {
        this.idClass = idClass;
        this.enumClass = enumClass;
    }

    @Override
    public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return EnumAide.of(enumClass, p.readValueAs(idClass));
    }

}
