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
        WatchDeleteListener<T>,
        WatchCreateListener<T>,
        WatchLoadListener<T>,
        WatchUpdateListener<T> {

    @Override
    default void onCreate(NameNodesWatcher<T> watcher, NameNode<T> node) {
    }

    @Override
    default void onDelete(NameNodesWatcher<T> watcher, NameNode<T> node) {
    }

    @Override
    default void onLoad(NameNodesWatcher<T> watcher, List<NameNode<T>> nodes) {

    }

    @Override
    default void onUpdate(NameNodesWatcher<T> watcher, NameNode<T> node) {
    }

}
