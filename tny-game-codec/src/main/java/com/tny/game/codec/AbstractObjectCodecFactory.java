package com.tny.game.codec;

import com.google.common.collect.ImmutableSet;
import org.springframework.util.MimeType;

import java.lang.reflect.*;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/19 6:27 下午
 */
public abstract class AbstractObjectCodecFactory implements ObjectCodecorFactory {

    private final Set<MimeType> supportMimeTypes;

    public AbstractObjectCodecFactory(Collection<MimeType> supportMimeTypes) {
        this.supportMimeTypes = ImmutableSet.copyOf(supportMimeTypes);
    }

    @Override
    public Collection<MimeType> getMediaTypes() {
        return this.supportMimeTypes;
    }

    protected <T> Class<T> loadClassFrom(Type type) {
        Class<?> valueClass = null;
        if (type instanceof Class) {
            valueClass = (Class<?>)type;
        }
        if (type instanceof ParameterizedType) {
            valueClass = (Class<?>)((ParameterizedType)type).getRawType();
        }
        if (valueClass == null) {
            throw new IllegalArgumentException(format("unsupported type {}}", type));
        }
        return as(valueClass);
    }

}