package com.tny.game.namespace.consistenthash;

/**
 * 对等节点(实体节点)
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 13:26
 **/
public interface ShardingNode {

    /**
     * @return 分区节点 id
     */
    String getHashKey();

}
