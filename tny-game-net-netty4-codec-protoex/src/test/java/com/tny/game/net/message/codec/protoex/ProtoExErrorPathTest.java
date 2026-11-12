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
package com.tny.game.net.message.codec.protoex;

import com.tny.game.net.message.codec.*;
import io.netty.buffer.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 27（Wave-C）：protoex 体编解码错误路径——垃圾/截断输入必须以异常终结
 * （交由解码链按协议错误处置），不得静默产出半成品对象。
 */
class ProtoExErrorPathTest {

    private final ProtoExMessageBodyCodec<Object> codec = new ProtoExMessageBodyCodec<>();

    @Test
    @DisplayName("垃圾标签字节：解码抛异常而非返回伪对象")
    void garbageTagFailsFast() {
        ByteBuf garbage = Unpooled.wrappedBuffer(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        assertThrows(Exception.class, () -> codec.decode(garbage));
    }

    @Test
    @DisplayName("截断负载：解码抛异常（长度声明超可读的下游形态）")
    void truncatedPayloadFails() throws Exception {
        // 用真实编码产物截尾制造结构破坏
        TestMsgObject payload = new TestMsgObject();
        ByteBuf full = Unpooled.buffer();
        codec.encode(payload, full);
        assertTrue(full.readableBytes() > 1, "前置：编码产物非空");
        ByteBuf truncated = full.copy(0, Math.max(1, full.readableBytes() / 2));
        full.release();
        assertThrows(Exception.class, () -> codec.decode(truncated));
        truncated.release();
    }

}
