/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.google.common.collect.ImmutableList;
import com.tny.game.codec.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;
import org.springframework.util.MimeType;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.collection.CollectionAide.*;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/19 6:27 下午
 */
public class ProtobufObjectCodecFactory extends AbstractObjectCodecFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufObjectCodecFactory.class);

    private static final Collection<MimeType> DEFAULT_MIME_TYPES = ImmutableList
            .copyOf(MimeTypeAide.asList(ProtobufMimeType.PROTOBUF, ProtobufMimeType.PROTOBUF_WILDCARD));

    private static final Map<Class<?>, ProtobufObjectCodec<?>> CODEC_MAP = new ConcurrentHashMap<>();

    private static final ProtobufObjectCodecFactory FACTORY = new ProtobufObjectCodecFactory();

    public static ProtobufObjectCodecFactory getInstance() {
        return FACTORY;
    }

    public ProtobufObjectCodecFactory() {
        this(null);
    }

    public ProtobufObjectCodecFactory(Collection<MimeType> supportMimeTypes) {
        super(ifEmpty(supportMimeTypes, DEFAULT_MIME_TYPES));
    }

    @Override
    public <T> ObjectCodec<T> createCodec(Type type) {
        Class<T> valueClass = loadClassFrom(type);
        ObjectCodec<?> codec = CODEC_MAP.computeIfAbsent(valueClass, clazz -> {
            Asserts.checkNotNull(valueClass.getAnnotation(ProtobufClass.class), "class {} is miss {} annotation", clazz, ProtobufClass.class);
            LOGGER.info("ProtobufObject Load [{}] finish", valueClass);
            return new ProtobufObjectCodec<>(clazz);
        });
        return as(codec);
    }

    @Override
    public MimeType isCanCodec(Class<?> clazz) {
        ProtobufClass protobufClass = clazz.getAnnotation(ProtobufClass.class);
        if (protobufClass != null) {
            return ProtobufMimeType.PROTOBUF_MIME_TYPE;
        }
        return null;
    }

}
