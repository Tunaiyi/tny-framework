package com.tny.game.suite.base.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class DateTimeJsonSerializer extends JsonSerializer<DateTime> {

    private DateTimeFormatter format;

    public DateTimeJsonSerializer(DateTimeFormatter format) {
        this.format = format;
    }

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(value.toString(format));
    }

}
