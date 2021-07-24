package com.tny.game.common.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 2:39 下午
 */
public class InstantJsonSerializer extends JsonSerializer<Instant> {

    private static final InstantJsonSerializer INSTANT = new InstantJsonSerializer();

    public static InstantJsonSerializer getDefault() {
        return INSTANT;
    }

    public InstantJsonSerializer() {
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeObject(null);
        } else {
            gen.writeNumber(value.toEpochMilli());
        }
    }

}
