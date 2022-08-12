/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.codec.typeprotobuf;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.google.common.collect.ImmutableList;
import com.tny.game.codec.*;
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.common.collection.*;
import com.tny.game.common.concurrent.*;
import org.springframework.util.MimeType;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Collection;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/19 6:27 下午
 */
public class TypeProtobufObjectCodecFactory extends AbstractObjectCodecFactory {

    private static final Collection<MimeType> DEFAULT_MIME_TYPES = ImmutableList
            .copyOf(MimeTypeAide.asList(TypeProtobufMimeType.TYPE_PROTOBUF, TypeProtobufMimeType.TYPE_PROTOBUF_WILDCARD));

    private TypeProtobufObjectCodec<?> codec;

    public TypeProtobufObjectCodecFactory() {
        this(null, null);
    }

    public TypeProtobufObjectCodecFactory(Collection<MimeType> supportMimeTypes) {
        this(supportMimeTypes, null);
    }

    public TypeProtobufObjectCodecFactory(ThreadLocalVar<ByteArrayOutputStream> localBuffer) {
        this(null, localBuffer);
    }

    public TypeProtobufObjectCodecFactory(Collection<MimeType> supportMimeTypes, ThreadLocalVar<ByteArrayOutputStream> localBuffer) {
        super(CollectionAide.ifEmpty(supportMimeTypes, DEFAULT_MIME_TYPES));
        if (localBuffer == null) {
            this.codec = new TypeProtobufObjectCodec<>();
        } else {
            this.codec = new TypeProtobufObjectCodec<>(localBuffer);
        }
    }

    @Override
    public <T> ObjectCodec<T> createCodec(Type type) {
        return as(this.codec);
    }

    @Override
    public MimeType isCanCodec(Class<?> clazz) {
        ProtobufClass protobufClass = clazz.getAnnotation(ProtobufClass.class);
        TypeProtobuf typeProtobuf = clazz.getAnnotation(TypeProtobuf.class);
        if (protobufClass != null && typeProtobuf != null) {
            return TypeProtobufMimeType.TYPE_PROTOBUF_MIME_TYPE;
        }
        return null;
    }

}
