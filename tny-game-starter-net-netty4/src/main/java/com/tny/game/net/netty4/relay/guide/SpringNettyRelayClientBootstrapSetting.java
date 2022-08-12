/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay.guide;

import com.tny.game.net.netty4.relay.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 3:56 下午
 */
public class SpringNettyRelayClientBootstrapSetting extends NettyRelayClientBootstrapSetting {

    @NestedConfigurationProperty
    private SpringNettyRelayChannelSetting channel;

    public SpringNettyRelayClientBootstrapSetting() {
        super(new SpringNettyRelayChannelSetting());
    }

    @Override
    public SpringNettyRelayChannelSetting getChannel() {
        return as(super.getChannel());
    }

    public NettyRelayClientBootstrapSetting setChannel(SpringNettyRelayChannelSetting channel) {
        return super.setChannel(channel);
    }

}

