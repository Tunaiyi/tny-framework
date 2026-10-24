/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.*;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.tny.game.codec.exception.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 12:48 下午
 */
public final class ProtobufCodecManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProtobufCodecManager.class);

    private static final ProtobufCodecManager INSTANCE = new ProtobufCodecManager();

    private static final Map<Class<?>, Codec<?>> typeSchemeMap = new ConcurrentHashMap<>();

    private ProtobufCodecManager() {
    }

    public static ProtobufCodecManager getInstance() {
        return INSTANCE;
    }

    private static <T> Codec<T> createCodec(Class<T> type) {
        ProtobufClass protobufClass = type.getAnnotation(ProtobufClass.class);
        Asserts.checkNotNull(protobufClass, "{} class annotation {} no exist",
                type, ProtobufClass.class);
        return ProtobufProxy.create(type);
    }

    public <T> Codec<T> loadCodec(Class<T> valueClass) {
        Codec<?> codec = typeSchemeMap.get(valueClass);
        if (codec != null) {
            return as(codec);
        }
        try {
            Codec<T> newCodec = createCodec(valueClass);
            Codec<?> old = typeSchemeMap.putIfAbsent(valueClass, newCodec);
            if (old != null) {
                return as(old);
            }
            LOGGER.info("ProtobufCodec Load [{}]  finish", valueClass);
            return newCodec;
        } catch (Throwable e) {
            throw new ObjectCodecException(e, "Load {} class TypeProtobufScheme exception", valueClass);
        }
    }

}
