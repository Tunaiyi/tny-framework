package com.tny.game.namespace.consistenthash;

import com.tny.game.namespace.*;

/**
 * 分区节点(虚拟节点)
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 14:40
 **/
public abstract class ShardingPartition<N extends ShardingNode> implements Partition<N> {

    /**
     * 重hash
     *
     * @param hasher   hash 器
     * @param maxSlots 最大槽数
     */
    public abstract void hash(Hasher<PartitionedNode<N>> hasher, long maxSlots);

}
