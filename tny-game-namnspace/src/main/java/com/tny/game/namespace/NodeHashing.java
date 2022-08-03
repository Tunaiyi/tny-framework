package com.tny.game.namespace;

import com.tny.game.namespace.consistenthash.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * hash 节点存储器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 03:12
 **/
public interface NodeHashing<N extends ShardingNode> extends Sharding<N> {

    /**
     * @return 名字
     */
    String getName();

    /**
     * @return 路径
     */
    String getPath();

    CompletableFuture<NodeHashing<N>> start();

    CompletableFuture<List<Partition<N>>> register(N node);

    CompletableFuture<List<Partition<N>>> register(N node, Set<Long> slotIndexes);

    void shutdown();

}
