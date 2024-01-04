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

package com.tny.game.net.netty4.relay;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.clusters.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:49 下午
 */
public class NettyRemoteServeCluster extends BaseRemoteServeCluster {

    private final RemoteServeClusterContext clusterContext;

    public NettyRemoteServeCluster(RemoteServeClusterContext clusterContext) {
        super(clusterContext.getServeName(),
                clusterContext.getService(),
                clusterContext.getUsername(),
                clusterContext.getServeInstanceAllotStrategy(),
                clusterContext.getRelayLinkAllotStrategy());
        this.clusterContext = clusterContext;
    }

    @Override
    public RemoteServeClusterContext getContext() {
        return this.clusterContext;
    }

    public void heartbeat() {
        for (NetRelayServeInstance instance : this.instances()) {
            ExeAide.runQuietly(instance::heartbeat, LOGGER);
        }
    }

    //	/**
    //	 * @param url url
    //	 */
    //	public void connect(URL url, RelayConnectCallback callback) {
    //		guide.connect(url, callback);
    //	}
    //
    //	/**
    //	 * @param url url
    //	 */
    //	public void connect(URL url, long delayTime, RelayConnectCallback callback) {
    //		executorService.schedule(() -> guide.connect(url, callback), delayTime, TimeUnit.MILLISECONDS);
    //	}

}
