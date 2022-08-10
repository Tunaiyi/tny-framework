package com.tny.game.namespace.etcd;

import com.tny.game.namespace.consistenthash.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 03:54
 **/
public class TestShadingNode implements ShardingNode {

    private String key;

    public TestShadingNode() {
    }

    public TestShadingNode(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    TestShadingNode setKey(String key) {
        this.key = key;
        return this;
    }

}
