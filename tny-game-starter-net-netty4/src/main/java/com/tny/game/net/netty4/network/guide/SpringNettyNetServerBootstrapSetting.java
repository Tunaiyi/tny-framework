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

import com.tny.game.net.netty4.network.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
//@Order(HIGHEST_PRECEDENCE)
//@Configuration
public class SpringNettyNetServerBootstrapSetting extends NettyNetServerBootstrapSetting {

    @NestedConfigurationProperty
    private SpringNettyChannelSetting channel;

    public SpringNettyNetServerBootstrapSetting() {
        super(new SpringNettyChannelSetting());
    }

    @Override
    public SpringNettyChannelSetting getChannel() {
        return as(super.getChannel());
    }

    public SpringNettyNetServerBootstrapSetting setChannel(SpringNettyChannelSetting channel) {
        super.setChannel(channel);
        return this;
    }

}
