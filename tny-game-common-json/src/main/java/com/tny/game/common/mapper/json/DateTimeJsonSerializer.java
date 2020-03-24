package com.tny.game.common.mapper.json;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class DateTimeJsonSerializer extends JsonSerializer<ZonedDateTime> {

    private DateTimeFormatter format;

    public DateTimeJsonSerializer(DateTimeFormatter format) {
        this.format = format;
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(value.format(this.format));
    }

}
