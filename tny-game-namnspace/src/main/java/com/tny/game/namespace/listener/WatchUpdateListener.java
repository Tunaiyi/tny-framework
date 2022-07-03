package com.tny.game.namespace.listener;

import com.tny.game.namespace.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 02:46
 **/
public interface WatchUpdateListener<T> {

    /**
     * 更新
     *
     * @param node 更新节点
     */
    void onUpdate(NameNodesWatcher<T> watcher, NameNode<T> node);

}
