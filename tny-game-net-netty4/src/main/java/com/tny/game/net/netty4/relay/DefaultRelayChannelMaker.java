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
