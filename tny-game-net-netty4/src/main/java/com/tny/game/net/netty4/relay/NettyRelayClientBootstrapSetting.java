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
