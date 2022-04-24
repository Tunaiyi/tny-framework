package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.common.result.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class ResultCodeJsonSerializer extends JsonSerializer<ResultCode> {

    private static final ResultCodeJsonSerializer INSTANT = new ResultCodeJsonSerializer();

    public static ResultCodeJsonSerializer getDefault() {
        return INSTANT;
    }

    @Override
    public void serialize(ResultCode value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeNumber(value.getCode());
    }

}
