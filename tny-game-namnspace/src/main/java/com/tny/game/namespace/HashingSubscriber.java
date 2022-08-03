package com.tny.game.namespace;

import com.tny.game.namespace.consistenthash.*;
import com.tny.game.namespace.listener.*;

import java.util.List;

/**
 * 哈希节点订阅器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 03:40
 **/
public interface HashingSubscriber<T> {

    String getPath();

    void subscribe(List<? extends ShardingRange<?>> ranges);

    void subscribeAll();

    void unsubscribe();

    void addListener(WatchListener<T> listener);

    void removeListener(WatchListener<T> listener);

    void clearListener();

    void close();

}
