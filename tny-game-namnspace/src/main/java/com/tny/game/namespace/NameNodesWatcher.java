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

import com.tny.game.common.event.*;
import com.tny.game.namespace.listener.*;

import java.util.concurrent.CompletableFuture;

/**
 * 节点监听器
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 04:16
 **/
public interface NameNodesWatcher<T> {

    /**
     * @return 监控路径
     */
    String getWatchPath();

    /**
     * @return 是否是模糊匹配
     */
    boolean isMatch();

    /**
     * 监控
     *
     * @return 返回 future
     */
    CompletableFuture<NameNodesWatcher<T>> watch();

    /**
     * 停止监控
     */
    void unwatch();

    /**
     * @return 是否是停止监控
     */
    boolean isUnwatch();

    /**
     * @return 是否是监控
     */
    boolean isWatch();

    /**
     * @return 监控者时间
     */
    EventWatch<WatcherListener> watcherEvent();

    /**
     * @return 加载事件
     */
    EventWatch<WatchLoadListener<T>> loadEvent();

    /**
     * @return 创建事件
     */
    EventWatch<WatchCreateListener<T>> createEvent();

    /**
     * @return 更新事件
     */
    EventWatch<WatchUpdateListener<T>> updateEvent();

    /**
     * @return 删除事件
     */
    EventWatch<WatchDeleteListener<T>> deleteEvent();

    /**
     * 添加节点监听器
     *
     * @param listener 监听器
     */
    void addListener(WatchListener<T> listener);

    /**
     * 删除节点监听器
     *
     * @param listener 监听器
     */
    void removeListener(WatchListener<T> listener);

}
