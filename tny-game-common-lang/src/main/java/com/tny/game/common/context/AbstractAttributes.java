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

package com.tny.game.common.context;

import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.Supplier;

public abstract class AbstractAttributes implements Attributes {

    /**
     * @uml.property name="attributeMap"
     */
    private volatile transient Map<AttrKey<?>, Object> attributeMap = null;

    private volatile Lock lock = null;

    private Lock getLock() {
        if (this.lock != null) {
            return this.lock;
        }
        synchronized (this) {
            if (this.lock != null) {
                return this.lock;
            }
            this.lock = new ReentrantLock();
        }
        return this.lock;
    }

    private void writeLock() {
        this.getLock().lock();
    }

    private void writeUnlock() {
        this.getLock().unlock();
    }

    private void readLock() {
        this.getLock().lock();
    }

    private void readUnlock() {
        this.getLock().unlock();
    }

    protected AbstractAttributes() {
        this(false);
    }

    protected AbstractAttributes(boolean init) {
        if (init) {
            this.attributeMap = new HashMap<>();
        }
    }

    private Map<AttrKey<?>, Object> getMap() {
        Map<AttrKey<?>, Object> map = this.attributeMap;
        if (map != null) {
            return map;
        }
        this.writeLock();
        try {
            map = this.attributeMap;
            if (map == null) {
                this.attributeMap = map = new HashMap<>();
            }
            return map;
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(AttrKey<? extends T> key) {
        this.readLock();
        try {
            return (T) this.getMap().get(key);
        } finally {
            this.readUnlock();
        }
    }

    @Override
    public <T> T getAttribute(AttrKey<? extends T> key, T defaultValue) {
        this.readLock();
        try {
            T value = this.getAttribute(key);
            return value != null ? value : defaultValue;
        } finally {
            this.readUnlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T computeIfAbsent(AttrKey<? extends T> key, T value) {
        // 与 getAttribute/setAttribute 同一把锁：消除"锁路径 vs 无锁路径"双轨混用（属性容器并发安全契约）
        this.writeLock();
        try {
            return (T) this.getMap().computeIfAbsent(key, k -> value);
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T computeIfAbsent(AttrKey<? extends T> key, Supplier<T> value) {
        this.writeLock();
        try {
            return (T) this.getMap().computeIfAbsent(key, k -> value.get());
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T setIfAbsent(AttrKey<? extends T> key, T value) {
        this.writeLock();
        try {
            return (T) this.getMap().putIfAbsent(key, value);
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T removeAttribute(AttrKey<? extends T> key) {
        this.writeLock();
        try {
            return (T) this.getMap().remove(key);
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    public <T> void setAttribute(AttrKey<? extends T> key, T value) {
        this.writeLock();
        try {
            this.getMap().put(key, value);
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    public void setAttribute(Map<AttrKey<?>, ?> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        this.writeLock();
        try {
            this.getMap().putAll(map);
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    public void setAttribute(AttrEntry<?> entry) {
        if (entry == null) {
            return;
        }
        this.writeLock();
        try {
            this.getMap().put(entry.getKey(), entry.getValue());
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    public void setAttribute(Collection<AttrEntry<?>> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }
        this.writeLock();
        try {
            for (AttrEntry<?> entry : entries) {
                this.getMap().put(entry.getKey(), entry.getValue());
            }
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    public void setAttribute(AttrEntry<?>... entries) {
        if (entries.length == 0) {
            return;
        }
        this.setAttribute(Arrays.asList(entries));
    }

    @Override
    public void removeAttribute(Collection<AttrKey<?>> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        this.writeLock();
        try {
            for (Object key : keys) {
                this.getMap().remove(key);
            }
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    public Map<AttrKey<?>, Object> getAttributeMap() {
        this.readLock();
        try {
            Map<AttrKey<?>, Object> temp = this.attributeMap;
            if (temp == null) {
                return Collections.emptyMap();
            }
            return Collections.unmodifiableMap(temp);
        } finally {
            this.readUnlock();
        }
    }

    @Override
    public void clearAttribute() {
        this.writeLock();
        try {
            this.attributeMap = null;
        } finally {
            this.writeUnlock();
        }
    }

    @Override
    public boolean isEmpty() {
        this.readLock();
        try {
            return this.attributeMap == null || this.attributeMap.isEmpty();
        } finally {
            this.readUnlock();
        }
    }

}
