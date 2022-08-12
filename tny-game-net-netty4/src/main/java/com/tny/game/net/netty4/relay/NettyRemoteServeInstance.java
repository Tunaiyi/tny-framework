/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:50 下午
 */
public class NettyRemoteServeInstance extends BaseRemoteServeInstance {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyRemoteServeInstance.class);

    /**
     * 连接任务
     */
    private final NettyServeInstanceConnectMonitor monitor;

    public NettyRemoteServeInstance(NetRemoteServeCluster cluster, ServeNode node, NettyServeInstanceConnectMonitor monitor) {
        super(cluster, node);
        this.monitor = monitor;
    }

    @Override
    protected void prepareClose() {
        monitor.stop();
    }

}