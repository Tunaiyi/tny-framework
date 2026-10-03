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

import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.relay.packet.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.*;

import java.nio.ByteBuffer;

public class RelayPackEncodeHandler extends MessageToByteEncoder<Object> implements RelayCodecErrorHandler {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CODER);

    private final RelayPacketEncoder encoder;

    private final boolean closeOnError;

    public RelayPackEncodeHandler(RelayPacketEncoder encoder, boolean closeOnError) {
        this.encoder = encoder;
        this.closeOnError = closeOnError;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof ByteBuf) {
            out.writeBytes((ByteBuf) msg);
            return;
        } else if (msg instanceof byte[]) {
            out.writeBytes((byte[]) msg);
            return;
        }
        if (msg instanceof ByteBuffer) {
            out.writeBytes((ByteBuffer) msg);
            return;
        }
        if (msg instanceof RelayPacket) {
            try {
                this.encoder.encodeObject(ctx, (RelayPacket<?>) msg, out);
            } catch (Exception | Error exception) {
                handleOnEncodeError(LOGGER, ctx, exception, closeOnError);
                // 编码失败不得吞掉（原误调 handleOnDecodeError 且静默返回）：
                // 异常穿透使零字节上线、本次写回执失败（与消息侧编码契约同型）
                throw exception;
            }
            return;
        }
        throw NetCodecException.causeEncodeFailed("can not encode {}", msg.getClass());
    }

}
