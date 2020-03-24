package com.tny.game.common.mapper.json;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.common.result.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class ResultCodeJsonSerializer extends JsonSerializer<ResultCode> {

    @Override
    public void serialize(ResultCode value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeNumber(value.getCode());
    }

}
