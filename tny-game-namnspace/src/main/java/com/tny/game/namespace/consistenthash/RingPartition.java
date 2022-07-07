package com.tny.game.namespace.consistenthash;

/**
 * 分区节点(虚拟节点)
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 14:40
 **/
public abstract class RingPartition<N extends ShardingNode> implements Partition<N> {

    /**
     * 设置槽位
     *
     * @param slot 槽位索引
     */
    public abstract void setSlot(long slot);

}
