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
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.*;

import java.nio.ByteBuffer;

import static com.tny.game.net.application.NetLogger.*;

public class NetPacketEncodeHandler extends MessageToByteEncoder<Object> implements NetPacketCodecErrorHandler {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CODER);

    private final NetPacketEncoder encoder;

    private final boolean closeOnError;

    public NetPacketEncodeHandler(NetPacketEncoder encoder, boolean closeOnError) {
        this.encoder = encoder;
        this.closeOnError = closeOnError;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try (ProcessTracer ignored = MESSAGE_ENCODE_WATCHER.trace()) {
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
            if (msg instanceof Message) {
                try {
                    this.encoder.encodeObject(ctx, (Message) msg, out);
                } catch (Exception | Error e) {
                    // 编码失败不得吞掉（残帧上线 + 假成功会毒化对端流，net-protocol 编码契约）：
                    // 先按 closeOnError / ResultLevel 分级决定通道去留（与原契约正交），再抛出——
                    // MessageToByteEncoder 整体丢弃 out（零字节上线），写回执以编码异常失败完成。
                    handleOnEncodeError(LOGGER, ctx, e, closeOnError);
                    throw e;
                }
                return;
            }
        }
        throw NetCodecException.causeEncodeFailed("can not encode {}", msg.getClass());
    }

}
