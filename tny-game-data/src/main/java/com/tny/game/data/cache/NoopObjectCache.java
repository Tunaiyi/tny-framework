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
