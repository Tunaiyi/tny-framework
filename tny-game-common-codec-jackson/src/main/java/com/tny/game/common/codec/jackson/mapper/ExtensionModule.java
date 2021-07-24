package com.tny.game.common.codec.jackson.mapper;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 3:22 下午
 */
public class ExtensionModule extends SimpleModule {

    public ExtensionModule() {
        extensionModule(this, Instant.class, InstantJsonSerializer.getDefault(), InstantJsonDeserializer.getDefault());
    }

    private <T> void extensionModule(SimpleModule module, Class<T> clazz, JsonSerializer<T> serializer, JsonDeserializer<T> deserializer) {
        module.addSerializer(clazz, serializer);
        module.addDeserializer(clazz, deserializer);
    }

}
