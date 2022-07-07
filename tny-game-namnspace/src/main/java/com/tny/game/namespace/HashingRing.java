package com.tny.game.namespace;

import com.tny.game.namespace.consistenthash.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * hash 环
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 03:12
 **/
public interface HashingRing<P extends ShardingNode> extends Sharding<P> {

    CompletableFuture<HashingRing<P>> start();

    CompletableFuture<List<RingPartition<P>>> register(P node);

    void shutdown();

}
