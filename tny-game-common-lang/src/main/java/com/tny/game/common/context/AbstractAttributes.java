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
        if (this.attributeMap != null) {
            return this.attributeMap;
        } else {
            if (this.attributeMap != null) {
                return this.attributeMap;
            }
            this.attributeMap = new HashMap<>();
        }
        return this.attributeMap;
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
        Map<AttrKey<?>, Object> map = this.getMap();
        return (T) map.computeIfAbsent(key, k -> value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T computeIfAbsent(AttrKey<? extends T> key, Supplier<T> value) {
        Map<AttrKey<?>, Object> map = this.getMap();
        return (T) map.computeIfAbsent(key, k -> value.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T setIfAbsent(AttrKey<? extends T> key, T value) {
        Map<AttrKey<?>, Object> map = this.getMap();
        return (T) map.putIfAbsent(key, value);
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
