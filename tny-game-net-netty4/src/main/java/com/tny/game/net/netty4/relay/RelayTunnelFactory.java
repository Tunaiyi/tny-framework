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

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 3:28 下午
 */
public class RelayTunnelFactory implements NettyTunnelFactory {

    private final RemoteRelayExplorer remoteRelayExplorer;

    public RelayTunnelFactory(RemoteRelayExplorer remoteRelayExplorer) {
        this.remoteRelayExplorer = remoteRelayExplorer;
    }

    @Override
    public <T> NetTunnel<T> create(long id, Channel channel, NetworkContext context) {
        MessageTransporter transport = new NettyChannelMessageTransporter(channel);
        DoneResult<RemoteRelayTunnel<T>> result = remoteRelayExplorer.createTunnel(id, transport, context);
        if (result.isFailure()) {
            throw new NetGeneralException(result.getCode());
        }
        return result.get();
    }

}
