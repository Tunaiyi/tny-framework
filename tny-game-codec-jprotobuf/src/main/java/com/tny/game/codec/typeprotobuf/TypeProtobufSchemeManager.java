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
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.codec.typeprotobuf.exception.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 12:48 下午
 */
public final class TypeProtobufSchemeManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(TypeProtobufSchemeManager.class);

    private static final TypeProtobufSchemeManager INSTANCE = new TypeProtobufSchemeManager();

    private static final Map<Integer, TypeProtobufScheme<?>> idSchemeMap = new ConcurrentHashMap<>();

    private static final Map<Class<?>, TypeProtobufScheme<?>> typeSchemeMap = new ConcurrentHashMap<>();

    private TypeProtobufSchemeManager() {
    }

    public static TypeProtobufSchemeManager getInstance() {
        return INSTANCE;
    }

    public <T> TypeProtobufScheme<T> getScheme(int id) {
        TypeProtobufScheme<T> scheme = as(idSchemeMap.get(id));
        return Asserts.checkNotNull(scheme, "TypeProtobuf Id {} TypeProtobufScheme is no exist", id);
    }

    public <T> TypeProtobufScheme<T> loadScheme(Class<T> valueClass) {
        TypeProtobufScheme<?> scheme = typeSchemeMap.get(valueClass);
        if (scheme != null) {
            return as(scheme);
        }
        try {
            Asserts.checkNotNull(valueClass.getAnnotation(TypeProtobuf.class), "class {} is miss {} annotation", valueClass, TypeProtobuf.class);
            Asserts.checkNotNull(valueClass.getAnnotation(ProtobufClass.class), "class {} is miss {} annotation", valueClass, ProtobufClass.class);
            TypeProtobufScheme<T> newScheme = new TypeProtobufScheme<>(valueClass);
            TypeProtobufScheme<?> old = typeSchemeMap.putIfAbsent(newScheme.getType(), newScheme);
            if (old != null) {
                return as(old);
            }
            old = idSchemeMap.put(newScheme.getId(), newScheme);
            if (old != null && old.getType() != newScheme.getType()) {
                throw new IllegalArgumentException(format("{} and {} are same ProtobufType id {}",
                        newScheme.getType(), old.getType(), old.getId()));
            }
            LOGGER.info("TypeProtobufScheme Load [({}) {}]  finish", newScheme.getId(), valueClass);
            return newScheme;
        } catch (Throwable e) {
            throw new TypeProtobufSchemeException(format("Load {} class TypeProtobufScheme exception", valueClass), e);
        }
    }

}
