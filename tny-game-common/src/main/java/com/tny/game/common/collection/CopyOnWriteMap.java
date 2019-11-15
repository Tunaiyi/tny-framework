package com.tny.game.common.collection;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.*;
import java.util.function.*;

/***
 * A thread-safe version of {@link Map} in which all operations that change the
 * Map are implemented by making a new copy of the underlying Map.
 * <p>
 * While the creation of a new Map can be expensive, this class is designed for
 * cases in which the primary function is to read data from the Map, not to
 * modify the Map. Therefore the operations that do not cause a change to this
 * class happen quickly and concurrently.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class CopyOnWriteMap<K, V> implements Map<K, V>, Cloneable {
    private volatile Map<K, V> internalMap;

    /***
     * Creates a new instance of CopyOnWriteMap.
     */
    public CopyOnWriteMap() {
        internalMap = new HashMap<>();
    }

    /***
     * Creates a new instance of CopyOnWriteMap with the specified initial size
     *
     * @param initialCapacity The initial size of the Map.
     */
    public CopyOnWriteMap(int initialCapacity) {
        internalMap = new HashMap<>(initialCapacity);
    }

    /***
     * Creates a new instance of CopyOnWriteMap in which the initial data being
     * held by this map is contained in the supplied map.
     *
     * @param data A Map containing the initial contents to be placed into this
     *             class.
     */
    public CopyOnWriteMap(Map<K, V> data) {
        internalMap = new HashMap<>(data);
    }

    /***
     * Adds the provided key and value to this map.
     *
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(K key, V value) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<>(internalMap);
            V val = newMap.put(key, value);
            internalMap = newMap;
            return val;
        }
    }

    /***
     * Removed the value and key from this map based on the provided key.
     *
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public V remove(Object key) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<>(internalMap);
            V val = newMap.remove(key);
            internalMap = newMap;
            return val;
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<>(internalMap);
            boolean result = newMap.remove(key, value);
            internalMap = newMap;
            return result;
        }
    }

    public Collection<V> removeAll(Collection<?> keys) {
        synchronized (this) {
            Collection<V> valuse = new ArrayList<>();
            Map<K, V> newMap = new HashMap<>(internalMap);
            for (Object k : keys) {
                V value = newMap.remove(k);
                if (value != null)
                    valuse.add(value);
            }
            internalMap = newMap;
            return valuse;
        }
    }

    /***
     * Inserts all the keys and values contained in the provided map to this
     * map.
     *
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> newData) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<>(internalMap);
            newMap.putAll(newData);
            internalMap = newMap;
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<>(internalMap);
            V val = newMap.putIfAbsent(key, value);
            internalMap = newMap;
            return val;
        }
    }

    @Override
    public V replace(K key, V value) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<>(internalMap);
            V val = newMap.replace(key, value);
            internalMap = newMap;
            return val;
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        synchronized (this) {
            Map<K, V> newMap = new HashMap<>(internalMap);
            newMap.replaceAll(function);
            internalMap = newMap;
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, oldValue) ||
            (curValue == null && !containsKey(key))) {
            return false;
        }
        synchronized (this) {
            curValue = get(key);
            if (!Objects.equals(curValue, oldValue) ||
                (curValue == null && !containsKey(key))) {
                return false;
            }
            put(key, newValue);
        }
        return true;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = get(key);
        if (v != null)
            return v;
        synchronized (this) {
            v = get(key);
            if (v != null)
                return v;
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }
        return v;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        if (get(key) != null) {
            synchronized (this) {
                V oldValue;
                if ((oldValue = get(key)) == null)
                    return null;
                V newValue = remappingFunction.apply(key, oldValue);
                if (newValue != null) {
                    put(key, newValue);
                    return newValue;
                } else {
                    remove(key);
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        synchronized (this) {
            V oldValue = get(key);
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue == null) {
                // delete mapping
                if (oldValue != null || containsKey(key)) {
                    // something to remove
                    remove(key);
                    return null;
                } else {
                    // nothing to do. Leave things as they were.
                    return null;
                }
            } else {
                // add or replace old mapping
                put(key, newValue);
                return newValue;
            }
        }
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        synchronized (this) {
            V oldValue = get(key);
            V newValue = (oldValue == null) ? value :
                         remappingFunction.apply(oldValue, value);
            if (newValue == null) {
                remove(key);
            } else {
                put(key, newValue);
            }
            return newValue;
        }
    }

    /***
     * Removes all entries in this map.
     *
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        synchronized (this) {
            internalMap = new HashMap<>();
        }
    }

    // ==============================================
    // ==== Below are methods that do not modify ====
    // ====         the internal Maps            ====
    // ==============================================

    /***
     * Returns the number of key/value pairs in this map.
     *
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        return internalMap.size();
    }

    /***
     * Returns true if this map is empty, otherwise false.
     *
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    /***
     * Returns true if this map contains the provided key, otherwise this method
     * return false.
     *
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    /***
     * Returns true if this map contains the provided value, otherwise this
     * method returns false.
     *
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return internalMap.containsValue(value);
    }

    /***
     * Returns the value associated with the provided key from this map.
     *
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
        return internalMap.get(key);
    }

    /***
     * This method will return a read-only {@link Set}.
     */
    @Override
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    /***
     * This method will return a read-only {@link Collection}.
     */
    @Override
    public Collection<V> values() {
        return internalMap.values();
    }

    /***
     * This method will return a read-only {@link Set}.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return internalMap.entrySet();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override
    public String toString() {
        return this.internalMap.toString();
    }
}
