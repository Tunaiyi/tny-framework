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
import com.tny.game.net.relay.packet.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.*;

import java.util.List;

public class RelayPackDecodeHandler extends ByteToMessageDecoder implements RelayCodecErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CODER);

    private final RelayPacketDecoder decoder;

    private final boolean closeOnError;

    public RelayPackDecodeHandler(RelayPacketDecoder decoder, boolean closeOnError) {
        this.decoder = decoder;
        this.closeOnError = closeOnError;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            while (in.readableBytes() > 0) {
                Object object = this.decoder.decodeObject(ctx, in);
                if (object instanceof RelayPacket) {
                    out.add(object);
                } else {
                    break;
                }
            }
        } catch (Throwable exception) {
            handleOnDecodeError(LOGGER, ctx, exception, closeOnError);
        }
    }

}
