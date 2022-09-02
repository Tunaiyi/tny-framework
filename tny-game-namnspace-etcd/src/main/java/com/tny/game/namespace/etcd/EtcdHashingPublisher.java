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
package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.sharding.*;

import java.util.concurrent.CompletableFuture;

/**
 * 哈希节点发布器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 19:50
 **/
public class EtcdHashingPublisher<K, T> extends EtcdHashing<T> implements HashingPublisher<K, T> {

    private volatile Lessee lessee;

    private volatile CompletableFuture<Lessee> lesseeFuture;

    private final Hasher<T> valueHasher;

    public EtcdHashingPublisher(String path, long maxSlots, Hasher<T> valueHasher, ObjectMimeType<T> mineType, NamespaceExplorer explorer) {
        super(path, maxSlots, mineType, explorer);
        this.valueHasher = valueHasher;
    }

    @Override
    public CompletableFuture<Lessee> lease() {
        return lease(60000);
    }

    @Override
    public CompletableFuture<Lessee> lease(long ttl) {
        Lessee lessee = this.lessee;
        if (lessee != null) {
            return CompletableFuture.completedFuture(lessee);
        }
        var lesseeFuture = this.lesseeFuture;
        if (lesseeFuture != null) {
            return lesseeFuture;
        }
        synchronized (this) {
            lessee = this.lessee;
            if (lessee != null) {
                return CompletableFuture.completedFuture(lessee);
            }
            lesseeFuture = this.lesseeFuture;
            if (lesseeFuture != null) {
                return lesseeFuture;
            }
            return this.lesseeFuture = explorer.lease("Publisher#" + path, ttl)
                    .thenApply(l -> {
                        this.lessee = l;
                        this.lesseeFuture = null;
                        return l;
                    });
        }
    }

    @Override
    public String pathOf(K key, T value) {
        var hashValue = valueHash(value);
        return NamespacePathNames.nodePath(path, slotName(hashValue), key);
    }

    @Override
    public CompletableFuture<NameNode<T>> publish(K key, T value) {
        var valuePath = pathOf(key, value);
        if (lessee != null) {
            return explorer.save(valuePath, mineType, value, lessee);
        } else {
            return explorer.save(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> operate(K key, T value, Publishing<T> operate) {
        var valuePath = pathOf(key, value);
        return operate.doPublish(explorer, valuePath, value, mineType, lessee);
    }

    @Override
    public CompletableFuture<NameNode<T>> publishIfAbsent(K key, T value) {
        var valuePath = pathOf(key, value);
        if (lessee != null) {
            return explorer.add(valuePath, mineType, value, lessee);
        } else {
            return explorer.add(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> publishIfExist(K key, T value) {
        var valuePath = pathOf(key, value);
        if (lessee != null) {
            return explorer.update(valuePath, mineType, value, lessee);
        } else {
            return explorer.update(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> revoke(K key, T value) {
        var valuePath = pathOf(key, value);
        return explorer.removeAndGet(valuePath, mineType);
    }

    private long valueHash(T value) {
        return Math.abs(valueHasher.hash(value, 0, maxSlots));
    }

    @Override
    protected long getMaxSlots() {
        return maxSlots;
    }

}
