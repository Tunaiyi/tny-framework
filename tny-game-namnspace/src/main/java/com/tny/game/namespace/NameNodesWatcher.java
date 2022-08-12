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

import com.tny.game.common.event.firer.*;
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
    EventSource<WatcherListener> watcherEvent();

    /**
     * @return 加载事件
     */
    EventSource<WatchLoadListener<T>> loadEvent();

    /**
     * @return 创建事件
     */
    EventSource<WatchCreateListener<T>> createEvent();

    /**
     * @return 更新事件
     */
    EventSource<WatchUpdateListener<T>> updateEvent();

    /**
     * @return 删除事件
     */
    EventSource<WatchDeleteListener<T>> deleteEvent();

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
