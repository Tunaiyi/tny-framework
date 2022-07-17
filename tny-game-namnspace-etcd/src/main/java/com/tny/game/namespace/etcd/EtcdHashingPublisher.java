package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;

import java.util.concurrent.CompletableFuture;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 哈希节点发布器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 19:50
 **/
public class EtcdHashingPublisher<T> implements HashingPublisher<T> {

    private final NamespaceExplorer explorer;

    private final String path;

    private final ObjectMineType<T> mineType;

    private final Hasher<T> hasher;

    private final HashAlgorithm algorithm;

    private volatile Lessee lessee;

    private volatile CompletableFuture<Lessee> lesseeFuture;

    public EtcdHashingPublisher(String path, Hasher<T> hasher, HashAlgorithm algorithm, ObjectMineType<T> mineType, NamespaceExplorer explorer) {
        this.explorer = explorer;
        this.path = path;
        this.mineType = mineType;
        this.hasher = hasher;
        this.algorithm = ifNull(algorithm, HashAlgorithms.getDefault());
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
    public CompletableFuture<NameNode<T>> publish(T value) {
        var id = hasher.toNodeId(value);
        var hashValue = hasher.hash(algorithm, value);
        var valuePath = NamespacePaths.nodePath(path, algorithm.alignDigits(hashValue), id);
        System.out.println("publish : " + valuePath);
        if (lessee != null) {
            return explorer.save(valuePath, mineType, value, lessee);
        } else {
            return explorer.save(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> handle(T value, Publishing<T> publishing) {
        var id = hasher.toNodeId(value);
        var hashValue = hasher.hash(algorithm, value);
        var valuePath = NamespacePaths.nodePath(path, algorithm.alignDigits(hashValue), id);
        return publishing.publish(explorer, valuePath, value, lessee);
    }

    @Override
    public CompletableFuture<NameNode<T>> publishIfAbsent(T value) {
        var id = hasher.toNodeId(value);
        var hashValue = hasher.hash(algorithm, value);
        var valuePath = NamespacePaths.nodePath(path, algorithm.alignDigits(hashValue), id);
        if (lessee != null) {
            return explorer.add(valuePath, mineType, value, lessee);
        } else {
            return explorer.add(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> publishIfExist(T value) {
        var id = hasher.toNodeId(value);
        var hashValue = hasher.hash(algorithm, value);
        var valuePath = NamespacePaths.nodePath(path, algorithm.alignDigits(hashValue), id);
        if (lessee != null) {
            return explorer.update(valuePath, mineType, value, lessee);
        } else {
            return explorer.update(valuePath, mineType, value);
        }
    }

    @Override
    public CompletableFuture<NameNode<T>> revoke(T value) {
        var id = hasher.toNodeId(value);
        var hashValue = hasher.hash(algorithm, value);
        var valuePath = NamespacePaths.nodePath(path, algorithm.alignDigits(hashValue), id);
        return explorer.removeAndGet(valuePath, mineType);
    }

}
