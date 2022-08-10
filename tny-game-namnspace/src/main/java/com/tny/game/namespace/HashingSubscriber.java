package com.tny.game.namespace;

import com.tny.game.codec.*;
import com.tny.game.namespace.consistenthash.*;
import com.tny.game.namespace.listener.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 哈希节点订阅器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 03:40
 **/
public interface HashingSubscriber<T> {

    String getPath();

    ObjectMineType<T> getMineType();

    CompletableFuture<Void> subscribe(List<? extends ShardingRange<?>> ranges);

    CompletableFuture<Void> subscribeAll();

    void unsubscribe();

    void addListener(WatchListener<T> listener);

    void removeListener(WatchListener<T> listener);

    void clearListener();

    void close();

}
