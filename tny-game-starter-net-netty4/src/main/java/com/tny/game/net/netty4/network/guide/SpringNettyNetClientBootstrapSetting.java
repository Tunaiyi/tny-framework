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

package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.application.*;
import com.tny.game.net.netty4.network.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 只是为了生成配置说明
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
public class SpringNettyNetClientBootstrapSetting extends NettyNetClientBootstrapSetting {

    @NestedConfigurationProperty
    private SpringNettyChannelSetting channel;

    @NestedConfigurationProperty
    private ClientConnectorSetting connector;

    public SpringNettyNetClientBootstrapSetting() {
        super(new SpringNettyChannelSetting());
    }

    @Override
    public SpringNettyChannelSetting getChannel() {
        return as(super.getChannel());
    }

    public NettyNetClientBootstrapSetting setChannel(SpringNettyChannelSetting channel) {
        return super.setChannel(channel);
    }

    @Override
    public ClientConnectorSetting getConnector() {
        return super.getConnector();
    }

    @Override
    public NettyNetClientBootstrapSetting setConnector(ClientConnectorSetting connector) {
        super.setConnector(connector);
        return this;
    }

}
