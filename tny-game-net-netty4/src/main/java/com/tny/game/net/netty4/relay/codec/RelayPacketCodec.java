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

package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.link.*;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 6:03 下午
 */
public interface RelayPacketCodec {

    default RelayLink getRelayPipe(ChannelHandlerContext ctx) {
        return ctx.channel().attr(NettyRelayAttrKeys.RELAY_LINK).get();
    }

    default RelayLink loadOrCreateRelayPipe(ChannelHandlerContext ctx, long id) {
        return ctx.channel().attr(NettyRelayAttrKeys.RELAY_LINK).get();
    }

}
