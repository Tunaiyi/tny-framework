package com.tny.game.namespace.listener;

import com.tny.game.namespace.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 02:46
 **/
public interface WatchDeleteListener<T> {

    /**
     * 删除
     *
     * @param node 删除节点
     */
    void onDelete(NameNodesWatcher<T> watcher, NameNode<T> node);

}
