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
public abstract class AbstractObjectCodecFactory implements ObjectCodecFactory {

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
            valueClass = (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            valueClass = (Class<?>) ((ParameterizedType) type).getRawType();
        }
        if (valueClass == null) {
            throw new IllegalArgumentException(format("unsupported type {}}", type));
        }
        return as(valueClass);
    }

}
