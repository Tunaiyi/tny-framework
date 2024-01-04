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

package com.tny.game.data.mongodb;

import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.javassist.*;
import com.tny.game.common.utils.*;
import com.tny.game.data.*;
import com.tny.game.data.mongodb.exception.*;
import org.springframework.data.annotation.Id;

import java.lang.reflect.*;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 2:59 下午
 */
public class MongoEntityIdConverter<K extends Comparable<?>, O> implements EntityIdConverter<K, O, K> {

    private static final EntityIdAccessor NO_OPERATION = new EntityIdAccessor() {

        @Override
        public <T> T getId(Object object) {
            throw new UnsupportedOperationException(format("{} 不存在 {} 注解", object.getClass(), Id.class));
        }
    };

    private final EntityIdAccessor accessor;

    public MongoEntityIdConverter(Class<?> entityClass) {
        this.accessor = createAccessor(entityClass);
    }

    private EntityIdAccessor createAccessor(Class<?> entityClass) {
        EntityIdAccessor newOne = IdFieldEntityIdAccessor.accessor(entityClass);
        if (newOne != null) {
            return newOne;
        }
        newOne = IdMethodEntityIdAccessor.accessor(entityClass);
        if (newOne != null) {
            return newOne;
        }
        return NO_OPERATION;
    }

    @Override
    public K keyToId(K key) {
        return (K) key;
    }

    @Override
    public K entityToId(O object) {
        return accessor.getId(object);
    }

    private interface EntityIdAccessor {

        <T> T getId(Object object);

    }

    private static class IdFieldEntityIdAccessor implements EntityIdAccessor {

        private final Field field;

        private static IdFieldEntityIdAccessor accessor(Class<?> entityClass) {
            List<Field> fields = ReflectAide.getDeepFieldsByAnnotation(entityClass, Id.class);
            if (fields.isEmpty()) {
                return null;
            }
            Asserts.checkState(fields.size() == 1, "{} @Id fields size > 1, {}", entityClass, fields);
            Field field = fields.get(0);
            return new IdFieldEntityIdAccessor(field);
        }

        private IdFieldEntityIdAccessor(Field field) {
            this.field = field;
            this.field.setAccessible(true);
        }

        @Override
        public <T> T getId(Object object) {
            try {
                return as(field.get(object));
            } catch (IllegalAccessException e) {
                throw new GetEntityIdException(e);
            }
        }

    }

    private static class IdMethodEntityIdAccessor implements EntityIdAccessor {

        private final MethodAccessor methodAccessor;

        private static IdMethodEntityIdAccessor accessor(Class<?> entityClass) {
            List<Method> methods = ReflectAide.getDeepMethodsByAnnotation(entityClass, Id.class);
            if (methods.isEmpty()) {
                return null;
            }
            Asserts.checkState(methods.size() == 1, "{} @Id fields size > 1, {}", entityClass, methods);
            Method method = methods.get(0);
            MethodAccessor methodAccessor = new JSsistMethodAccessor(method);
            return new IdMethodEntityIdAccessor(methodAccessor);
        }

        private IdMethodEntityIdAccessor(MethodAccessor methodAccessor) {
            this.methodAccessor = methodAccessor;
        }

        @Override
        public <T> T getId(Object object) {
            try {
                return as(methodAccessor.invoke(object));
            } catch (InvocationTargetException e) {
                throw new GetEntityIdException(e);
            }
        }

    }

}
