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

package com.tny.game.net.netty4.network;

import com.tny.game.net.application.configuration.*;
import com.tny.game.net.netty4.*;

public class NettyNetServerBootstrapSetting extends CommonServerBootstrapSetting implements NettyBootstrapSetting {

    private NettyChannelSetting channel;

    public NettyNetServerBootstrapSetting() {
        this(null, null);
    }

    public NettyNetServerBootstrapSetting(String encodeBodyCodec, String decodeBodyCodec) {
        channel = new NettyChannelSetting(encodeBodyCodec, decodeBodyCodec);
    }

    public NettyNetServerBootstrapSetting(NettyChannelSetting channel) {
        this.channel = channel;
    }

    public NettyNetServerBootstrapSetting(String name) {
        this.setName(name);
    }

    @Override
    public NettyChannelSetting getChannel() {
        return channel;
    }

    public NettyNetServerBootstrapSetting setChannel(NettyChannelSetting channel) {
        this.channel = channel;
        return this;
    }

}
