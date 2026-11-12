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

package com.tny.game.data.cache;

/**
 * <p>
 */
public class NoopObjectCache<K extends Comparable<?>, O> implements ObjectCache<K, O> {

    /**
     * 缓存方案
     */
    private EntityScheme scheme;

    public NoopObjectCache() {
    }

    public NoopObjectCache(EntityScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public EntityScheme getScheme() {
        return scheme;
    }

    @Override
    public O get(K key) {
        return null;
    }

    @Override
    public void put(K key, O value) {
    }

    @Override
    public boolean remove(K key, O value) {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

}
