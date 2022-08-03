package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;

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

    private final Hasher<K> hasher;

    public EtcdHashingPublisher(String path, Hasher<K> hasher, ObjectMineType<T> mineType, NamespaceExplorer explorer) {
        super(path, mineType, explorer);
        this.hasher = hasher;
    }

    @Override
    public String getPath() {
        return path;
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
    public CompletableFuture<NameNode<T>> publish(K key, T value) {
        var hashValue = hashCode(key);
        var valuePath = NamespacePathNames.nodePath(path, slotName(hashValue), key);
        System.out.println("publish : " + valuePath);
        if (lessee != null) {
            return explorer.save(valuePath, mineType, value, lessee);
        } else {
            return explorer.save(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> publish(K key, T value, Publishing<T> publishing) {
        var hashValue = hashCode(key);
        var valuePath = NamespacePathNames.nodePath(path, slotName(hashValue), key);
        return publishing.doPublish(explorer, valuePath, value, mineType, lessee);
    }

    @Override
    public CompletableFuture<NameNode<T>> publishIfAbsent(K key, T value) {
        var hashValue = hashCode(key);
        var valuePath = NamespacePathNames.nodePath(path, slotName(hashValue), key);
        if (lessee != null) {
            return explorer.add(valuePath, mineType, value, lessee);
        } else {
            return explorer.add(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> publishIfExist(K key, T value) {
        var hashValue = hashCode(key);
        var valuePath = NamespacePathNames.nodePath(path, slotName(hashValue), key);
        if (lessee != null) {
            return explorer.update(valuePath, mineType, value, lessee);
        } else {
            return explorer.update(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> revoke(K key, T value) {
        var hashValue = hashCode(key);
        var valuePath = NamespacePathNames.nodePath(path, slotName(hashValue), key);
        return explorer.removeAndGet(valuePath, mineType);
    }

    private long hashCode(K key) {
        return Math.abs(hasher.hash(key, 0));
    }

    @Override
    protected long getMaxSlots() {
        return hasher.getMax();
    }

}
