package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 2:39 下午
 */
public class InstantJsonDeserializer extends JsonDeserializer<Instant> {

    private static final InstantJsonDeserializer INSTANT = new InstantJsonDeserializer();

    public static InstantJsonDeserializer getDefault() {
        return INSTANT;
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext context) throws IOException {
        long milli = p.getValueAsLong();
        return Instant.ofEpochMilli(milli);
    }

}