package com.tny.game.doc.output;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.tny.game.doc.table.*;


/**
 * Created by Kun Yang on 2017/4/8.
 */
class JSONExporter implements Exporter {

    private ObjectMapper objectMapper = new ObjectMapper();

    JSONExporter() {
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                    .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
    }

    @Override
    public String output(ConfigTable table) {
        try {
            return objectMapper.writeValueAsString(table);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getHead() {
        return "";
    }

}
