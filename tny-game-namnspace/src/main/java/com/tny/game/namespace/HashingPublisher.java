/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

    ObjectMineType<T> getMineType();

    CompletableFuture<Lessee> lease();

    CompletableFuture<Lessee> lease(long ttl);

    String pathOf(K key, T value);

    CompletableFuture<NameNode<T>> publish(K key, T value);

    CompletableFuture<NameNode<T>> operate(K key, T value, Publishing<T> publishing);

    CompletableFuture<NameNode<T>> publishIfAbsent(K key, T value);

    CompletableFuture<NameNode<T>> publishIfExist(K key, T value);

    CompletableFuture<NameNode<T>> revoke(K key, T value);

}
