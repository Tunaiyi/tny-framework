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

package com.tny.game.net.rpc;

import com.tny.game.net.rpc.setting.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 4:10 下午
 */
public class RpcRemoteSetting {

    private long invokeTimeout = 5000L;

    private Class<? extends FirstRpcRouter> defaultRpcRemoteRouter = FirstRpcRouter.class;

    private List<RpcClusterSetting> clusters = new ArrayList<>();

    public long getInvokeTimeout() {
        return invokeTimeout;
    }

    public RpcRemoteSetting setInvokeTimeout(long invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
        return this;
    }

    public Class<? extends FirstRpcRouter> getDefaultRpcRemoteRouter() {
        return defaultRpcRemoteRouter;
    }

    public RpcRemoteSetting setDefaultRpcRemoteRouter(Class<? extends FirstRpcRouter> defaultRpcRemoteRouter) {
        this.defaultRpcRemoteRouter = defaultRpcRemoteRouter;
        return this;
    }

    public List<RpcClusterSetting> getClusters() {
        return clusters;
    }

    public RpcRemoteSetting setClusters(List<RpcClusterSetting> clusters) {
        this.clusters = clusters;
        return this;
    }
}
