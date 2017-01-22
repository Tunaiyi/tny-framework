package com.tny.game.oplog.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Message;
import com.tny.game.common.utils.protobuf.Protobuf2JsonFormat;
import com.tny.game.oplog.Alter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class OpLogMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogMapper.class);

    private static ObjectMapper mapper = new ObjectMapper();

    private Exception exception;

    private CountDownLatch latch = new CountDownLatch(1);

    private static final String ALTER_VALUE_FAILED = "v";
    private static final String ALTER_LATELY_FAILED = "l";

    public static ObjectMapper getMapper() {
        return mapper;
    }

    @SuppressWarnings("rawtypes")
    private static final JsonDeserializer<Alter> ALTER_DESERIALIZER = new JsonDeserializer<Alter>() {

        private Object parserValue(JsonParser jsonParser, JsonToken token) throws IOException {
            if (token == JsonToken.VALUE_NUMBER_INT) {
                return jsonParser.getIntValue();
            } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                return jsonParser.getDoubleValue();
            } else if (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE) {
                return jsonParser.getBooleanValue();
            } else if (token == JsonToken.VALUE_STRING) {
                return jsonParser.getText();
            }
            return null;
        }

        @Override
        public Alter<?> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            JsonToken token = jsonParser.getCurrentToken();
            if (token == JsonToken.START_OBJECT) {
                return this.parserAlter(jsonParser);
            } else {
                Object value = this.parserValue(jsonParser, token);
                return Alter.of(value);
            }
        }

        private Alter<Object> parserAlter(JsonParser jsonParser) throws IOException {
            Object[] values = new Object[2];
            JsonToken token = null;
            while (token == null || token != JsonToken.END_OBJECT)
                token = this.parserAlterValue(jsonParser, values);
            return Alter.of(values[0], values[1]);
        }

        private JsonToken parserAlterValue(JsonParser jsonParser, Object[] values) throws IOException {
            JsonToken token = jsonParser.nextToken();
            if (token == JsonToken.FIELD_NAME) {
                String name = jsonParser.getText();
                token = jsonParser.nextToken();
                Object value = this.parserValue(jsonParser, token);
                if (value != null) {
                    if (name.equals(ALTER_VALUE_FAILED)) {
                        values[0] = value;
                    } else if (name.equals(ALTER_LATELY_FAILED)) {
                        values[1] = value;
                    }
                }
            }
            return token;
        }

    };

    private static final JsonSerializer<Alter> ALTER_SERIALIZER = new JsonSerializer<Alter>() {

        @Override
        public void serialize(Alter changer, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (!changer.isChange()) {
                this.writeValue(generator, changer.getValue());
            } else {
                generator.writeStartObject();
                generator.writeFieldName(ALTER_VALUE_FAILED);
                this.writeValue(generator, changer.getValue());
                generator.writeFieldName(ALTER_LATELY_FAILED);
                this.writeValue(generator, changer.getLately());
                generator.writeEndObject();
            }
        }

        private void writeValue(JsonGenerator generator, Object value) throws IOException {
            if (value == null) {
                generator.writeNull();
            } else if (value instanceof Number) {
                generator.writeNumber(value.toString());
            } else if (value instanceof Boolean) {
                generator.writeBoolean((Boolean) value);
            } else {
                generator.writeString(value.toString());
            }
        }

    };

    private static final JsonSerializer<Message> MESSAGE_SERIALIZER = new JsonSerializer<Message>() {

        @Override
        public void serialize(Message message, JsonGenerator generator, SerializerProvider provider) throws IOException {
            StringBuilder builder = new StringBuilder();
            Protobuf2JsonFormat.printFiles(message, builder);
            generator.writeStartObject();
            generator.writeRaw(builder.toString());
            generator.writeEndObject();
        }

    };

    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Message.class, MESSAGE_SERIALIZER);
        module.addSerializer(Alter.class, ALTER_SERIALIZER);
        module.addDeserializer(Alter.class, ALTER_DESERIALIZER);
        mapper.registerModule(module);
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

}