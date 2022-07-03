package com.tny.game.namespace.listener;

import com.tny.game.namespace.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/30 19:06
 **/
public interface WatcherListener {

    /**
     * 创建
     */
    void onCompleted(NameNodesWatcher<?> watcher);

    /**
     * 删除
     *
     * @param cause 异常
     */
    void onError(NameNodesWatcher<?> watcher, Throwable cause);

}
