package com.tny.game.namespace.consistenthash.listener;

import com.tny.game.namespace.consistenthash.*;

import java.util.List;

/**
 * 分片监听器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 02:46
 **/
public interface ShardingListener<N extends ShardingNode> {

    /**
     * 增加分片改变
     *
     * @param sharding 改变分片
     */
    void onChange(Sharding<N> sharding, List<Partition<N>> partitions);

    /**
     * 增加分片改变
     *
     * @param sharding 改变分片
     */
    void onRemove(Sharding<N> sharding, List<Partition<N>> partitions);

}
