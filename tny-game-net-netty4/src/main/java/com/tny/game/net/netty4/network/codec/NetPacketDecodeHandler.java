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

package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.runtime.*;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.*;

import java.util.List;

import static com.tny.game.net.application.NetLogger.*;

public class NetPacketDecodeHandler extends ByteToMessageDecoder implements NetPacketCodecErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CODER);

    private final NetPacketDecoder decoder;

    private final boolean closeOnError;

    private final NetPacketDecodeMarker marker = new NetPacketDecodeMarker();

    public NetPacketDecodeHandler(NetPacketDecoder decoder) {
        this(decoder, false);
    }

    public NetPacketDecodeHandler(NetPacketDecoder decoder, boolean closeOnError) {
        this.decoder = decoder;
        this.closeOnError = closeOnError;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try (ProcessTracer ignored = MESSAGE_DECODE_WATCHER.trace()) {
            try {
                while (in.readableBytes() > 0) {
                    Object messageObject = this.decoder.decodeObject(ctx, in, this.marker);
                    if (messageObject instanceof Message) {
                        out.add(messageObject);
                    } else {
                        break;
                    }
                }
            } catch (Throwable exception) {
                handleOnEncodeError(LOGGER, ctx, exception, closeOnError);
            }
        }

    }

}
