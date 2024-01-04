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

package com.tny.game.net.netty4.network;

import com.tny.game.net.application.configuration.*;
import com.tny.game.net.netty4.*;

public class NettyNetClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyBootstrapSetting {

    private NettyChannelSetting channel;

    public NettyNetClientBootstrapSetting() {
        this.channel = new NettyChannelSetting(null, null);
    }

    public NettyNetClientBootstrapSetting(NettyChannelSetting channel) {
        this.channel = channel;
    }

    public NettyNetClientBootstrapSetting(String name) {
        this.setName(name);
    }

    @Override
    public NettyChannelSetting getChannel() {
        return channel;
    }

    public NettyNetClientBootstrapSetting setChannel(NettyChannelSetting channel) {
        this.channel = channel;
        return this;
    }

}
