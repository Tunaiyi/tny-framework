package com.tny.game.namespace.listener;

import com.tny.game.namespace.*;

import java.util.List;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 02:46
 **/
public interface WatchLoadListener<T> {

    /**
     * 创建
     *
     * @param nodes 创建节点
     */
    void onLoad(NameNodesWatcher<T> watcher, List<NameNode<T>> nodes);

}
