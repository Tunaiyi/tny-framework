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
