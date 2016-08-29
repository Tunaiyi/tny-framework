package com.tny.game.common.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class DateTimeJsonDeserializer extends JsonDeserializer<DateTime> {

    private DateTimeFormatter format;

    public DateTimeJsonDeserializer(DateTimeFormatter format) {
        this.format = format;
    }

    @Override
    public DateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return format.parseDateTime(parser.getValueAsString());
    }
}
