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
 * @author : kgtny
 * @date : 2020/7/30 5:43 下午
 */
public class ObjectCodecService {

    private volatile Map<MimeType, ObjectCodecFactory> codecFactoryMap = ImmutableMap.of();

    private final Map<Type, ObjectCodecHolder<?>> objectCodecHolderMap = new ConcurrentHashMap<>();

    public ObjectCodecService(Collection<ObjectCodecFactory> codecorFactories) {
        this.setObjectCodecFactories(codecorFactories);
    }

    public boolean isSupported(MimeType type) {
        for (MimeType mimeType : this.codecFactoryMap.keySet()) {
            if (mimeType.isCompatibleWith(type)) {
                return true;
            }
        }
        return false;
    }

    public <T> ObjectCodec<T> codec(Type type) {
        ObjectCodecHolder<T> holder = as(this.objectCodecHolderMap.get(type));
        if (holder != null) {
            return holder.getDefaultCodec();
        }
        return as(this.objectCodecHolderMap.computeIfAbsent(type, ObjectCodecHolder::new).getDefaultCodec());
    }

    public <T> ObjectCodec<T> codec(Type type, String mineType) {
        ObjectCodecHolder<T> holder = as(this.objectCodecHolderMap.get(type));
        if (holder != null) {
            return holder.loadOrCreateObjectCodec(mineType);
        }
        return as(this.objectCodecHolderMap.computeIfAbsent(type, ObjectCodecHolder::new).loadOrCreateObjectCodec(mineType));
    }

    private <T> ObjectCodecHolder<T> holder(Type type) {
        return as(this.objectCodecHolderMap.computeIfAbsent(type, ObjectCodecHolder::new));
    }

    public <T> byte[] encodeToBytes(T value) {
        ObjectCodecHolder<T> holder = holder(value.getClass());
        try {
            return holder.getDefaultCodec().encode(value);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "encode {} to default format {} exception", value.getClass(), holder.getDefaultFormat());
        }
    }

    public <T> T decodeByBytes(Class<T> type, byte[] data) {
        ObjectCodecHolder<T> holder = holder(type);
        try {
            return holder.getDefaultCodec().decode(data);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "decode {} from default format {} exception", type, holder.getDefaultFormat());
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

    public <T> T decodeByBytes(Class<T> type, byte[] data, String mineType) {
        ObjectCodec<T> codec = codec(type, mineType);
        try {
            return codec.decode(data);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "decode {} from {} format exception", type, mineType);
        }
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
            this.defaultFormat = loadTypeFormat(type);
            ObjectCodec<T> codec = createObjectCodec(this.defaultFormat);
            this.defaultCodec = codec;
            this.objectCodecs.put(this.defaultFormat, codec);
        }

        public ObjectCodec<T> getDefaultCodec() {
            return this.defaultCodec;
        }

        public String getDefaultFormat() {
            return this.defaultFormat;
        }

        public ObjectCodec<T> loadOrCreateObjectCodec(String format) {
            return this.objectCodecs.computeIfAbsent(format, t -> createObjectCodec(format));
        }

        public ObjectCodec<T> createObjectCodec(String format) {
            MimeType mimeType = MimeType.valueOf(format);
            ObjectCodecFactory codecFactory = ObjectCodecService.this.codecFactoryMap.get(mimeType);
            if (codecFactory != null) {
                return codecFactory.createCodec(this.type);
            }
            Optional<ObjectCodec<Object>> objectCodecOpt = ObjectCodecService.this.codecFactoryMap.entrySet()
                    .stream().filter(e -> e.getKey().includes(mimeType))
                    .map(e -> e.getValue().createCodec(this.type))
                    .filter(Objects::nonNull)
                    .findFirst();
            return as(objectCodecOpt.orElseThrow(() -> new NullPointerException(format("{} ObjectCodecFactory is null", mimeType))));
        }

        private String loadTypeFormat(Type type) {
            Class<?> clazz = null;
            if (type instanceof Class) {
                clazz = as(type);
                Codable codableObject = clazz.getAnnotation(Codable.class);
                if (codableObject != null) {
                    return MimeTypeAide.getMimeType(codableObject);
                }
            } else if (type instanceof ParameterizedType) {
                clazz = as(((ParameterizedType)type).getRawType());
                Codable codecableObject = clazz.getAnnotation(
                        Codable.class);
                if (codecableObject != null) {
                    return MimeTypeAide.getMimeType(codecableObject);
                }
            }
            throw new NullPointerException(format("{} no exist {} annotation", clazz, Codable.class));
        }

    }

}
