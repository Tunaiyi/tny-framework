package com.tny.game.suite.base.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tny.game.common.result.ResultCode;

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
