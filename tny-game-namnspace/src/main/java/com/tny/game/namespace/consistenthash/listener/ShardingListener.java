package com.tny.game.namespace.consistenthash.listener;

import com.tny.game.namespace.consistenthash.*;

/**
 * 分片监听器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 02:46
 **/
public interface ShardingListener<N extends ShardingNode> {

    /**
     * 分片改变
     *
     * @param sharding 改变分片
     */
    void onChange(Sharding<N> sharding);

}
