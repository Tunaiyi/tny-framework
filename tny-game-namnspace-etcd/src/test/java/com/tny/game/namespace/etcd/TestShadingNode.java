/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.namespace.etcd;

import com.tny.game.namespace.sharding.*;

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
