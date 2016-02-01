package com.tny.game.suite.oplog;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.Message;
import com.tny.game.LogUtils;
import com.tny.game.common.utils.protobuf.Protobuf2JsonFormat;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.oplog.Alter;
import com.tny.game.oplog.Snapshot;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.filter.ClassFilterHelper;
import com.tny.game.suite.utils.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Component
@Profile({"suite.base", "suite.all"})
public class OpLogMapper implements ServerPreStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogMapper.class);

    private ObjectMapper mapper = new ObjectMapper();

    private Exception exception;

    private CountDownLatch latch = new CountDownLatch(1);

    private static final String ALTER_VALUE_FAILED = "v";
    private static final String ALTER_LATELY_FAILED = "l";

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

    @PostConstruct
    public void init() throws InstantiationException, IllegalAccessException {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Message.class, MESSAGE_SERIALIZER);
        module.addSerializer(Alter.class, ALTER_SERIALIZER);
        module.addDeserializer(Alter.class, ALTER_DESERIALIZER);
        OpLogMapper.this.mapper.registerModule(module);
        OpLogMapper.this.mapper.setSerializationInclusion(Include.NON_NULL);
        LOGGER.info("启动初始化OpLogMapper任务!");
        Thread thread = new Thread(() -> {
            Class<?> clazz = null;
            try {
                ClassScanner scanner = new ClassScanner()
                        .addFilter(ClassFilterHelper.ofInclude((reader) ->
                                ClassFilterHelper.matchSuper(reader, Snapshot.class)
                        ));
                Set<Class<?>> classes = scanner.getClasses(Configs.getScanPathArray());
                for (Class<?> cl : classes) {
                    Snapshot snapShot = (Snapshot) cl.newInstance();
                    mapper.registerSubtypes(new NamedType(cl, snapShot.getType().toString()));
                }
            } catch (Throwable e) {
                OpLogMapper.this.exception = new IllegalStateException(e);
                throw new RuntimeException(LogUtils.format("获取 {} 类错误", clazz), OpLogMapper.this.exception);
            } finally {
                OpLogMapper.this.latch.countDown();
            }
        });
        thread.start();
        LOGGER.info("初始化OpLogMapper任务完成!");
    }

    @Override
    public InitLevel getInitLevel() {
        return InitLevel.LEVEL_10;
    }

    @Override
    public boolean waitInitialized() throws Exception {
        this.latch.await();
        if (this.exception != null)
            throw this.exception;
        return true;
    }

    @Override
    public void initialize() throws Exception {
        waitInitialized();
    }

    public static void main(String[] args) throws Exception {

        OpLogMapper mapper = new OpLogMapper();
        mapper.init();
        mapper.initialize();
        //		Test value = new Test(100, 20.9);
        //		Test read = null;
        //      String json = "";
        //		json = OpLogMapper.getMapper().writeValueAsString(value);
        //		read = OpLogMapper.getMapper().readValue(json, Test.class);
        //		System.out.println(json + " ==> " + read);
        //
        //		value.uList1.update(20);
        //		json = OpLogMapper.getMapper().writeValueAsString(value);
        //		read = OpLogMapper.getMapper().readValue(json, Test.class);
        //		System.out.println(json + " ==> " + read);
        //
        //		value.uList2.update(20.9);
        //		json = OpLogMapper.getMapper().writeValueAsString(value);
        //		read = OpLogMapper.getMapper().readValue(json, Test.class);
        //		System.out.println(json + " ==> " + read);
        //
        //		value.uList2.update(30.9);
        //		json = OpLogMapper.getMapper().writeValueAsString(value);
        //		read = OpLogMapper.getMapper().readValue(json, Test.class);
        //		System.out.println(json + " ==> " + read);
        //
        //		value.uList2.update(20.9);
        //		json = OpLogMapper.getMapper().writeValueAsString(value);
        //		read = OpLogMapper.getMapper().readValue(json, Test.class);
        //		System.out.println(json + " ==> " + read);

        //        TestList test = new TestList(1, 2, 3, 455, 666);
        //        json = OpLogMapper.getMapper().writeValueAsString(test);
        //        System.out.println(json);
        //        json = "{\"uList\":[1,2,3,455,666]}";
        //        test = OpLogMapper.getMapper().readValue(json, TestList.class);
        //        System.out.println(test);
    }

}
