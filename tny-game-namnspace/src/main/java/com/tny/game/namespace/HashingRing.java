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
public interface HashingRing<N extends ShardingNode> extends Sharding<N> {

    String getName();

    CompletableFuture<HashingRing<N>> start();

    CompletableFuture<List<ShardingPartition<N>>> register(N node);

    void shutdown();

}
