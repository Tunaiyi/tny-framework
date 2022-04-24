package com.tny.game.data.mongodb.mapper;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import org.bson.types.Decimal128;

import java.io.IOException;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 11:56
 */
public class MapToListSerializer extends JsonSerializer<Decimal128> {

    @Override
    public void serialize(Decimal128 value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        ObjectCodec codec = gen.getCodec();
        gen.setCodec(null);
        gen.writeObject(value);
        gen.setCodec(codec);
    }

}