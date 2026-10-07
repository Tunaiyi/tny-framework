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
package com.tny.game.namespace;

import com.tny.game.codec.*;

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

    ObjectMimeType<T> getMineType();

    CompletableFuture<Lessee> lease();

    CompletableFuture<Lessee> lease(long ttl);

    String pathOf(K key, T value);

    CompletableFuture<NameNode<T>> publish(K key, T value);

    CompletableFuture<NameNode<T>> operate(K key, T value, Publishing<T> publishing);

    CompletableFuture<NameNode<T>> publishIfAbsent(K key, T value);

    CompletableFuture<NameNode<T>> publishIfExist(K key, T value);

    CompletableFuture<NameNode<T>> revoke(K key, T value);

}
