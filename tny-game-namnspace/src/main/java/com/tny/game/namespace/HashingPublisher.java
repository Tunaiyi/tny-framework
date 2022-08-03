package com.tny.game.namespace;

import java.util.concurrent.CompletableFuture;

/**
 * Hashing 节点发布器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 03:40
 **/
public interface HashingPublisher<K, T> {

    String getPath();

    CompletableFuture<Lessee> lease();

    CompletableFuture<Lessee> lease(long ttl);

    CompletableFuture<NameNode<T>> publish(K key, T value);

    CompletableFuture<NameNode<T>> publish(K key, T value, Publishing<T> publishing);

    CompletableFuture<NameNode<T>> publishIfAbsent(K key, T value);

    CompletableFuture<NameNode<T>> publishIfExist(K key, T value);

    CompletableFuture<NameNode<T>> revoke(K key, T value);

}
