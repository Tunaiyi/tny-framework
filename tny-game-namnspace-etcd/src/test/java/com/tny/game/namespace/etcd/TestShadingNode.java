package com.tny.game.namespace.etcd;

import com.tny.game.namespace.consistenthash.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 03:54
 **/
public class TestShadingNode implements ShardingNode {

    private String id;

    public TestShadingNode() {
    }

    public TestShadingNode(String id) {
        this.id = id;
    }

    @Override
    public String getNodeId() {
        return id;
    }

    TestShadingNode setId(String id) {
        this.id = id;
        return this;
    }

}
