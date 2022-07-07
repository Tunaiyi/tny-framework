package com.tny.game.namespace.consistenthash;

/**
 * 分区
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 13:26
 **/
public interface Partition<N extends ShardingNode> {

    /**
     * @return 分区键值
     */
    String getKey();

    /**
     * @return 对应节点
     */
    N getNode();

    /**
     * @return 对应节点第几个分区
     */
    int getIndex();

    /**
     * @return 槽位id
     */
    long getSlot();

    default String getNodeId() {
        N node = this.getNode();
        if (node != null) {
            return node.getNodeId();
        }
        return null;
    }

}
