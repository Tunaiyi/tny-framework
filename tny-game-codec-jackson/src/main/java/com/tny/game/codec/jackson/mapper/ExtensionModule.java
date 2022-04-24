package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.common.result.*;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 3:22 下午
 */
public class ExtensionModule extends SimpleModule {

    public ExtensionModule() {
        extensionModule(Instant.class, InstantJsonSerializer.getDefault(), InstantJsonDeserializer.getDefault());
        extensionModule(ResultCode.class, ResultCodeJsonSerializer.getDefault(), ResultCodeJsonDeserializer.getDefault());
    }

    public <T> void extensionModule(Class<T> clazz, JsonSerializer<T> serializer, JsonDeserializer<T> deserializer) {
        this.addSerializer(clazz, serializer);
        this.addDeserializer(clazz, deserializer);
    }

}
