package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.function.Function;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 1:20 下午
 */

public class IdObjectSerializer<T> extends JsonSerializer<T> {

    private final Function<T, Integer> supplier;

    public IdObjectSerializer(Function<T, Integer> supplier) {
        this.supplier = (v) -> {
            if (v == null) {
                return null;
            }
            return supplier.apply(v);
        };
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeNumber(supplier.apply(value));
        }
    }

}
