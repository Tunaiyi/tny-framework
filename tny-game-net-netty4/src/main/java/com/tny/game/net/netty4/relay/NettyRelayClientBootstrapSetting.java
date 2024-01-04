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

import com.tny.game.net.application.configuration.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 1:50 下午
 */
public class NettyRelayClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyRelayBootstrapSetting {

    private NettyRelayChannelSetting channel;

    public NettyRelayClientBootstrapSetting() {
    }

    public NettyRelayClientBootstrapSetting(NettyRelayChannelSetting channel) {
        this.channel = channel;
    }

    @Override
    public NettyRelayChannelSetting getChannel() {
        return channel;
    }

    public NettyRelayClientBootstrapSetting setChannel(NettyRelayChannelSetting channel) {
        this.channel = channel;
        return this;
    }

}
