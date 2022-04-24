package com.tny.game.codec;

import com.google.common.collect.ImmutableMap;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.exception.*;
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

    private volatile Map<MimeType, ObjectCodecorFactory> codecorFactoryMap = ImmutableMap.of();

    private final Map<Type, ObjectCodecorHolder<?>> DEFAULT_OBJECT_HOLDER_MAP = new ConcurrentHashMap<>();

    public ObjectCodecService(Collection<ObjectCodecorFactory> codecorFactories) {
        this.setObjectCodecorFactories(codecorFactories);
    }

    public boolean isSupported(MimeType type) {
        for (MimeType mimeType : this.codecorFactoryMap.keySet()) {
            if (mimeType.isCompatibleWith(type)) {
                return true;
            }
        }
        return false;
    }

    public <T> ObjectCodec<T> codec(Type type) {
        ObjectCodecorHolder<T> holder = as(this.DEFAULT_OBJECT_HOLDER_MAP.get(type));
        if (holder != null) {
            return holder.getDefaultCodecor();
        }
        return as(this.DEFAULT_OBJECT_HOLDER_MAP.computeIfAbsent(type, ObjectCodecorHolder::new).getDefaultCodecor());
    }

    public <T> ObjectCodec<T> codec(Type type, String mineType) {
        ObjectCodecorHolder<T> holder = as(this.DEFAULT_OBJECT_HOLDER_MAP.get(type));
        if (holder != null) {
            return holder.loadOrCreateObjectCodec(mineType);
        }
        return as(this.DEFAULT_OBJECT_HOLDER_MAP.computeIfAbsent(type, ObjectCodecorHolder::new).loadOrCreateObjectCodec(mineType));
    }

    private <T> ObjectCodecorHolder<T> holder(Type type) {
        return as(this.DEFAULT_OBJECT_HOLDER_MAP.computeIfAbsent(type, ObjectCodecorHolder::new));
    }

    public <T> byte[] encodeToBytes(T value) {
        ObjectCodecorHolder<T> holder = holder(value.getClass());
        try {
            return holder.getDefaultCodecor().encode(value);
        } catch (IOException e) {
            throw new ObjectCodecException(e, "encode {} to default format {} exception", value.getClass(), holder.getDefaultFormat());
        }
    }

    public <T> T decodeByBytes(Class<T> type, byte[] data) {
        ObjectCodecorHolder<T> holder = holder(type);
        try {
            return holder.getDefaultCodecor().decode(data);
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

    public class ObjectCodecorHolder<T> {

        private final Type type;

        private final String defaultFormat;

        private final ObjectCodec<T> defaultCodecor;

        private final Map<String, ObjectCodec<T>> DEFAULT_OBJECT_CODEC_MAP = new ConcurrentHashMap<>();

        public ObjectCodecorHolder(Type type) {
            this.type = type;
            this.defaultFormat = loadTypeFormat(type);
            ObjectCodec<T> codecor = createObjectCodec(this.defaultFormat);
            this.defaultCodecor = codecor;
            this.DEFAULT_OBJECT_CODEC_MAP.put(this.defaultFormat, codecor);
        }

        public ObjectCodec<T> getDefaultCodecor() {
            return this.defaultCodecor;
        }

        public String getDefaultFormat() {
            return this.defaultFormat;
        }

        public ObjectCodec<T> loadOrCreateObjectCodec(String format) {
            return this.DEFAULT_OBJECT_CODEC_MAP.computeIfAbsent(format, t -> createObjectCodec(format));
        }

        public ObjectCodec<T> createObjectCodec(String format) {
            MimeType mimeType = MimeType.valueOf(format);
            ObjectCodecorFactory codecorFactory = ObjectCodecService.this.codecorFactoryMap.get(mimeType);
            if (codecorFactory != null) {
                return codecorFactory.createCodec(this.type);
            }
            Optional<ObjectCodec<Object>> objectCodecorOpt = ObjectCodecService.this.codecorFactoryMap.entrySet()
                    .stream().filter(e -> e.getKey().includes(mimeType))
                    .map(e -> e.getValue().createCodec(this.type))
                    .filter(Objects::nonNull)
                    .findFirst();
            return as(objectCodecorOpt.orElseThrow(() -> new NullPointerException(format("{} ObjectCodecorFactory is null", mimeType))));
        }

        private String loadTypeFormat(Type type) {
            Class<?> clazz = null;
            if (type instanceof Class) {
                clazz = as(type);
                Codecable codecableObject = clazz.getAnnotation(Codecable.class);
                if (codecableObject != null) {
                    return MimeTypeAide.getMimeType(codecableObject);
                }
            } else if (type instanceof ParameterizedType) {
                clazz = as(((ParameterizedType)type).getRawType());
                Codecable codecableObject = clazz.getAnnotation(
                        Codecable.class);
                if (codecableObject != null) {
                    return MimeTypeAide.getMimeType(codecableObject);
                }
            }
            throw new NullPointerException(format("{} no exist {} annotation", clazz, Codecable.class));
        }

    }

    protected void setObjectCodecorFactories(Collection<ObjectCodecorFactory> factories) {
        Map<MimeType, ObjectCodecorFactory> factoryHashMap = new HashMap<>();
        factories.forEach(f -> {
            f.getMediaTypes().forEach(m -> {
                ObjectCodecorFactory old = factoryHashMap.putIfAbsent(m, f);
                Asserts.checkArgument(old == null, "{} and {} MimeType {} is same", f, old, m);
            });
        });
        this.codecorFactoryMap = ImmutableMap.copyOf(factoryHashMap);
    }

}
