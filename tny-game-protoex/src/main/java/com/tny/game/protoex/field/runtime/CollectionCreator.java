package com.tny.game.protoex.field.runtime;

import com.tny.game.LogUtils;
import com.tny.game.protoex.ProtobufExException;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Collection创建器
 *
 * @author KGTny
 */
public class CollectionCreator {

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> createMap(Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            if (clazz == Map.class)
                return new HashMap<K, V>();
            else if (clazz == SortedMap.class)
                return new TreeMap<K, V>();
            else if (clazz == ConcurrentMap.class)
                return new ConcurrentHashMap<K, V>();
            throw new IllegalArgumentException(LogUtils.format("{}无法找到对应的Collection", clazz));
        } else {
            try {
                return (Map<K, V>) clazz.newInstance();
            } catch (Exception e) {
                throw ProtobufExException.causeBy(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> createCollection(Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            if (clazz == Collection.class || clazz == List.class)
                return new ArrayList<T>();
            else if (clazz == SortedSet.class)
                return new TreeSet<T>();
            else if (clazz == Set.class)
                return new HashSet<T>();
            else if (clazz == Queue.class || clazz == Deque.class)
                return new ArrayDeque<T>();
            throw new IllegalArgumentException(LogUtils.format("{}无法找到对应的Collection", clazz));
        } else {
            try {
                return (Collection<T>) clazz.newInstance();
            } catch (Exception e) {
                throw ProtobufExException.causeBy(e);
            }
        }
    }
}
