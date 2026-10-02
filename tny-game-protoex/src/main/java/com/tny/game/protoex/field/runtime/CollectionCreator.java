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
package com.tny.game.protoex.field.runtime;

import com.tny.game.protoex.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Collection创建器
 *
 * @author KGTny
 */
public class CollectionCreator {

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> createMap(Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            if (clazz == Map.class) {
                return new HashMap<>();
            } else if (clazz == SortedMap.class || clazz == NavigableMap.class) {
                return new TreeMap<>();
            } else if (clazz == ConcurrentMap.class) {
                return new ConcurrentHashMap<>();
            }
            throw new IllegalArgumentException(format("{}无法找到对应的Collection", clazz));
        } else {
            try {
                return (Map<K, V>) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw ProtobufExException.causeBy(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> createCollection(Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            if (clazz == Collection.class || clazz == List.class) {
                return new ArrayList<>();
            }
            if (clazz == SortedSet.class) {
                return new TreeSet<>();
            }
            if (clazz == Set.class) {
                return new HashSet<>();
            }
            if (clazz == Queue.class || clazz == Deque.class) {
                return new ArrayDeque<>();
            }
            throw new IllegalArgumentException(format("{}无法找到对应的Collection", clazz));
        } else {
            if (clazz == Object.class) {
                return new ArrayList<>();
            }
            try {
                return (Collection<T>) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw ProtobufExException.causeBy(e);
            }
        }
    }

}
