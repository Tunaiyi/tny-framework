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
package com.tny.game.namespace;

import com.tny.game.codec.*;
import com.tny.game.namespace.listener.*;
import com.tny.game.namespace.sharding.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 哈希节点订阅器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 03:40
 **/
public interface HashingSubscriber<T> {

    String getPath();

    ObjectMimeType<T> getMineType();

    CompletableFuture<Void> subscribe(List<? extends ShardingRange<?>> ranges);

    CompletableFuture<Void> subscribeAll();

    void unsubscribe();

    void addListener(WatchListener<T> listener);

    void removeListener(WatchListener<T> listener);

    void clearListener();

    void close();

}
