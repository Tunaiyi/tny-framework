package com.tny.game.namespace;

import java.util.concurrent.CompletableFuture;

/**
 * Hashing 节点发布器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 03:40
 **/
public interface HashingPublisher<T> {

    String getPath();

    CompletableFuture<Lessee> lease();

    CompletableFuture<Lessee> lease(long ttl);

    CompletableFuture<NameNode<T>> publish(T value);

    CompletableFuture<NameNode<T>> publishIfAbsent(T value);

    CompletableFuture<NameNode<T>> publishIfExist(T value);

    CompletableFuture<NameNode<T>> revoke(T value);

    CompletableFuture<NameNode<T>> handle(T value, Publishing<T> publishing);

}
