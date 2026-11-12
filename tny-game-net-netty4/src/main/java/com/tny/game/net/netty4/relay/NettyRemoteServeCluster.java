/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
