package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.common.result.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class ResultCodeJsonDeserializer extends JsonDeserializer<ResultCode> {

	private static final ResultCodeJsonDeserializer INSTANT = new ResultCodeJsonDeserializer();

	public static ResultCodeJsonDeserializer getDefault() {
		return INSTANT;
	}

	@Override
	public ResultCode deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return ResultCodes.of(parser.getIntValue());
	}

}
