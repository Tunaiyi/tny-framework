package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.function.Function;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 1:20 下午
 */

public class IdObjectDeserializer<T> extends JsonDeserializer<T> {

    private final Function<Integer, T> loader;

    public IdObjectDeserializer(Function<Integer, T> loader) {
        this.loader = loader;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object object = p.getCurrentValue();
        if (object == null) {
            return null;
        }
        return loader.apply(p.getIntValue());
    }

}
