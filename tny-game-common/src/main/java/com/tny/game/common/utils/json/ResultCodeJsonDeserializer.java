package com.tny.game.common.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodes;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class ResultCodeJsonDeserializer extends JsonDeserializer<ResultCode> {

    @Override
    public ResultCode deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ResultCodes.of(parser.getIntValue());
    }

}
