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
