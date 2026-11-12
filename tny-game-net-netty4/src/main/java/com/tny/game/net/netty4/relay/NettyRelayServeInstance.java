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

import com.tny.game.net.clusters.*;
import com.tny.game.net.relay.cluster.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:50 下午
 */
public class NettyRelayServeInstance extends BaseRelayServeInstance {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyRelayServeInstance.class);

    /**
     * 连接任务
     */
    private final NettyServeInstanceConnectMonitor monitor;

    public NettyRelayServeInstance(NetRemoteServeCluster cluster, ServeNode node, NettyServeInstanceConnectMonitor monitor) {
        super(cluster, node);
        this.monitor = monitor;
    }

    @Override
    protected void prepareClose() {
        monitor.stop();
    }

}