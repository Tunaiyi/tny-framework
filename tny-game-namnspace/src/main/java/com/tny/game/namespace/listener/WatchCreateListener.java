package com.tny.game.namespace.listener;

import com.tny.game.namespace.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 02:46
 **/
public interface WatchCreateListener<T> {

    /**
     * 创建
     *
     * @param node 创建节点
     */
    void onCreate(NameNodesWatcher<T> watcher, NameNode<T> node);

}
