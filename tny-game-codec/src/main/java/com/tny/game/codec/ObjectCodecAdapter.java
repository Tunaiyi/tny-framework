/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.codec;

import com.google.common.collect.ImmutableMap;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.exception.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.utils.*;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 05:22
 **/
public class ObjectCodecAdapter {

    private volatile Map<MimeType, ObjectCodecFactory> codecFactoryMap = ImmutableMap.of();

    private final Map<Type, ObjectCodecHolder<?>> objectCodecHolderMap = new ConcurrentHashMap<>();

    public ObjectCodecAdapter(Collection<ObjectCodecFactory> factories) {
        this.setObjectCodecFactories(factories);
    }

    public boolean isSupported(MimeType type) {
        for (MimeType mimeType : this.codecFactoryMap.keySet()) {
            if (mimeType.isCompatibleWith(type)) {
                return true;
            }
        }
        return false;
    }

    public <T> ObjectCodec<T> codec(ObjectMimeType<T> mineType) {
        ObjectCodecHolder<T> holder = holder(mineType);
        return mineType.hasMineType() ?
               holder.loadObjectCodec(mineType.getMineType()) :
               holder.getDefaultCodec();
    }

    public <T> ObjectCodec<T> codec(Class<?> codecForClass, String mineType) {
        ObjectCodecHolder<T> holder = holder(codecForClass);
        return holder.loadObjectCodec(mineType);
    }

    public <T> ObjectCodec<T> codec(Class<?> codecForClass) {
        ObjectCodecHolder<T> holder = holder(codecForClass);
        return holder.getDefaultCodec();
    }

    public <T> ObjectCodec<T> codec(Type codecForType) {
        ObjectCodecHolder<T> holder = holder(codecForType);
        return holder.getDefaultCodec();
    }

    public <T> ObjectCodec<T> codec(Type codecForType, String mineType) {
        ObjectCodecHolder<T> holder = holder(codecForType);
        return holder.loadObjectCodec(mineType);
    }

    public <T> byte[] encodeToBytes(T value) {
        ObjectCodec<T> codec = codec(value.getClass());
        try {
            return codec.encode(value);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "encode {} to default format {} exception", value.getClass(), codec);
        }
    }

    public <T> byte[] encodeToBytes(ObjectMimeType<T> mineType, T value) {
        ObjectCodec<T> codec = codec(mineType);
        try {
            return codec.encode(value);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "encode {} to default format {} exception", value.getClass(), codec);
        }
    }

    public <T> byte[] encodeToBytes(T value, String mineType) {
        ObjectCodec<T> codec = codec(value.getClass(), mineType);
        try {
            return codec.encode(value);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "encode {} to {} format exception", value.getClass(), mineType);
        }
    }

    public <T> T decodeByBytes(Class<T> type, byte[] data) {
        ObjectCodec<T> codec = codec(type);
        try {
            return codec.decode(data);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "decode {} from default format {} exception", type, codec);
        }
    }

    public <T> T decodeByBytes(ObjectMimeType<T> mineType, byte[] data) {
        ObjectCodec<T> codec = codec(mineType);
        try {
            return codec.decode(data);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "decode {} from default format {} exception", mineType, codec);
        }
    }

    public <T> T decodeByBytes(Class<T> type, byte[] data, String mineType) {
        ObjectCodec<T> codec = codec(type, mineType);
        try {
            return codec.decode(data);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "decode {} from {} format exception", type, mineType);
        }
    }

    private <T> ObjectCodecHolder<T> holder(Type type) {
        return as(this.objectCodecHolderMap.computeIfAbsent(type, ObjectCodecHolder::new));
    }

    private <T> ObjectCodecHolder<T> holder(ObjectMimeType<T> mineType) {
        return as(this.objectCodecHolderMap.computeIfAbsent(mineType.getType(), ObjectCodecHolder::new));
    }

    private void setObjectCodecFactories(Collection<ObjectCodecFactory> factories) {
        Map<MimeType, ObjectCodecFactory> factoryHashMap = new HashMap<>();
        factories.forEach(f -> {
            f.getMediaTypes().forEach(m -> {
                ObjectCodecFactory old = factoryHashMap.putIfAbsent(m, f);
                Asserts.checkArgument(old == null, "{} and {} MimeType {} is same", f, old, m);
            });
        });
        this.codecFactoryMap = ImmutableMap.copyOf(factoryHashMap);
    }

    private class ObjectCodecHolder<T> {

        private final Type type;

        private final String defaultFormat;

        private final ObjectCodec<T> defaultCodec;

        private final Map<String, ObjectCodec<T>> objectCodecs = new CopyOnWriteMap<>();

        public ObjectCodecHolder(Type type) {
            this.type = type;
            this.defaultFormat = loadMimeType(type);
            if (this.defaultFormat != null) {
                ObjectCodec<T> codec = createObjectCodec(this.defaultFormat);
                this.defaultCodec = codec;
                this.objectCodecs.put(this.defaultFormat, codec);
            } else {
                this.defaultCodec = null;
            }
        }

        public ObjectCodec<T> createObjectCodec(String format) {
            MimeType mimeType = MimeType.valueOf(format);
            ObjectCodecFactory codecFactory = ObjectCodecAdapter.this.codecFactoryMap.get(mimeType);
            if (codecFactory != null) {
                return codecFactory.createCodec(this.type);
            }
            Optional<ObjectCodec<Object>> objectCodecOpt = ObjectCodecAdapter.this.codecFactoryMap.entrySet()
                    .stream().filter(e -> e.getKey().includes(mimeType))
                    .map(e -> e.getValue().createCodec(this.type))
                    .filter(Objects::nonNull)
                    .findFirst();
            return as(objectCodecOpt.orElseThrow(() -> new NullPointerException(format("{} ObjectCodecFactory is null", mimeType))));
        }

        public ObjectCodec<T> getDefaultCodec() {
            return this.defaultCodec;
        }

        public String getDefaultFormat() {
            return this.defaultFormat;
        }

        public ObjectCodec<T> loadObjectCodec(String format) {
            return this.objectCodecs.computeIfAbsent(format, t -> createObjectCodec(format));
        }

        private String loadMimeType(Type type) {
            Class<?> clazz = null;
            if (type instanceof Class) {
                clazz = as(type);
                Codable codableObject = clazz.getAnnotation(Codable.class);
                if (codableObject != null) {
                    return MimeTypeAide.getMimeType(codableObject);
                }
            } else if (type instanceof ParameterizedType) {
                clazz = as(((ParameterizedType) type).getRawType());
                Codable codable = clazz.getAnnotation(Codable.class);
                if (codable != null) {
                    return MimeTypeAide.getMimeType(codable);
                }
            }
            return null;
            //            throw new NullPointerException(format("{} no exist {} annotation", clazz, Codable.class));
        }

    }

}
