/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.base.*;
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
            out.writeBytes((ByteBuf)msg);
            return;
        } else if (msg instanceof byte[]) {
            out.writeBytes((byte[])msg);
            return;
        }
        if (msg instanceof ByteBuffer) {
            out.writeBytes((ByteBuffer)msg);
            return;
        }
        if (msg instanceof RelayPacket) {
            try {
                this.encoder.encodeObject(ctx, (RelayPacket<?>)msg, out);
            } catch (Throwable exception) {
                handleOnDecodeError(LOGGER, ctx, exception, closeOnError);
            }
            return;
        }
        throw NetCodecException.causeEncodeFailed("can not encode {}", msg.getClass());
    }

}
