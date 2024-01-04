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

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.netty4.relay.codec.*;
import io.netty.channel.Channel;

@UnitInterface
public class DefaultRelayChannelMaker<C extends Channel> extends RelayPackChannelMaker<C> {

    public DefaultRelayChannelMaker() {
    }

    public DefaultRelayChannelMaker(RelayPacketEncoder encoder, RelayPacketDecoder decoder) {
        super(encoder, decoder);
    }

    @Override
    protected void postInitChannel(C channel) {

    }

}
