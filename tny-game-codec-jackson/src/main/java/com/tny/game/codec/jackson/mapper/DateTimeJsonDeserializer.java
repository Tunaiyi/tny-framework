package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class DateTimeJsonDeserializer extends JsonDeserializer<ZonedDateTime> {

    private final DateTimeFormatter format;

    public DateTimeJsonDeserializer(DateTimeFormatter format) {
        this.format = format;
    }

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ZonedDateTime.parse(parser.getValueAsString(), this.format);
    }

}
