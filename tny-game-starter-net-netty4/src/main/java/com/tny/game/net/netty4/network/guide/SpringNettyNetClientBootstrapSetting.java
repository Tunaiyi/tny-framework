/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.base.*;
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
    private ClientSetting connector;

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
    public ClientSetting getConnector() {
        return super.getConnector();
    }

    @Override
    public NettyNetClientBootstrapSetting setConnector(ClientSetting connector) {
        super.setConnector(connector);
        return this;
    }

}
