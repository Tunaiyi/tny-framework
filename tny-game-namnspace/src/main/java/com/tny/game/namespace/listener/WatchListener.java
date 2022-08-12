/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace.listener;

import com.tny.game.namespace.*;

import java.util.List;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/30 19:06
 **/
public interface WatchListener<T> extends
        WatchLoadListener<T>,
        WatchCreateListener<T>,
        WatchUpdateListener<T>,
        WatchDeleteListener<T> {

    @Override
    default void onLoad(NameNodesWatcher<T> watcher, List<NameNode<T>> nodes) {

    }

    @Override
    default void onCreate(NameNodesWatcher<T> watcher, NameNode<T> node) {
    }

    @Override
    default void onUpdate(NameNodesWatcher<T> watcher, NameNode<T> node) {
    }

    @Override
    default void onDelete(NameNodesWatcher<T> watcher, NameNode<T> node) {
    }

}
