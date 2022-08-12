/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.message.codec.protoex;

import com.tny.game.net.codec.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.transport.*;
import com.tny.game.protoex.*;
import io.netty.buffer.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 */
public class MessageCodecTest {

    private ProtoExMessageBodyCodec<Object> objectCodec = new ProtoExMessageBodyCodec<>();

    private MessageHeaderCodec messageHeaderCodec = new DefaultMessageHeaderCodec();

    private NettyMessageCodec codec = new DefaultNettyMessageCodec(
            this.objectCodec, messageHeaderCodec, MessageRelayStrategy.NO_RELAY_STRATEGY);

    private NettyMessageCodec codecNoDecode = new DefaultNettyMessageCodec(
            this.objectCodec, messageHeaderCodec, (head) -> true);

    private CommonMessageFactory messageFactory = new CommonMessageFactory();

    private NetMessage createMessage(Object body) {
        long uid = 5000L;
        return this.messageFactory.create(1L, MessageContexts.request(Protocols.protocol(100_100), body));
    }

    @Test
    public void encodeDecode() throws Exception {
        TestMsgObject body = new TestMsgObject(100, "I am test body!");
        TestMsgObject attachment = new TestMsgObject(100, "I am test attachment!");
        ByteBuf data;
        OctetMessageBody bodyBytes;
        // 正常解析 有 Body
        NetMessage encodeMessage = createMessage(body);
        data = ByteBufAllocator.DEFAULT.heapBuffer();
        data.markReaderIndex();
        this.codec.encode(encodeMessage, data);
        Message decodeMessage = this.codec.decode(data, this.messageFactory);
        assertEquals(encodeMessage.getHead(), decodeMessage.getHead());
        assertIterableEquals(encodeMessage.bodyAs(MessageParamList.class), decodeMessage.bodyAs(List.class));
        // 不解析 Body, 有 Body
        data.resetReaderIndex();
        Message noDecodeBodyMessage = this.codecNoDecode.decode(data, this.messageFactory);
        assertEquals(encodeMessage.getHead(), noDecodeBodyMessage.getHead());
        bodyBytes = noDecodeBodyMessage.bodyAs(OctetMessageBody.class);
        assertNotNull(bodyBytes);

        ByteBuf bodyBuf = (ByteBuf)bodyBytes.getBody();
        byte[] bodyArray = new byte[bodyBuf.readableBytes()];
        bodyBuf.readBytes(bodyArray);
        ProtoExReader bodyReader = new ProtoExReader(bodyArray);
        List<Object> paramList = bodyReader.readMessage(List.class);
        assertIterableEquals(encodeMessage.bodyAs(MessageParamList.class), paramList);
        // 正常解析 无 Body
        NetMessage encodeNoBodyMessage = createMessage(null);

        data.clear();
        this.codec.encode(encodeNoBodyMessage, data);
        Message decodeNoBodyMessage = this.codec.decode(data, this.messageFactory);
        assertEquals(encodeNoBodyMessage.getHead(), decodeNoBodyMessage.getHead());
        paramList = decodeNoBodyMessage.bodyAs(List.class);
        assertTrue(paramList.isEmpty());
        // 不解析 Body, 无 Body
        data.clear();
        this.codecNoDecode.encode(encodeNoBodyMessage, data);
        Message noDecodeBodyNoBodyMessage = this.codecNoDecode.decode(data, this.messageFactory);
        assertEquals(encodeNoBodyMessage.getHead(), noDecodeBodyNoBodyMessage.getHead());
        bodyBytes = noDecodeBodyNoBodyMessage.bodyAs(ByteBufMessageBody.class);
        bodyBuf = (ByteBuf)bodyBytes.getBody();
        bodyArray = new byte[bodyBuf.readableBytes()];
        bodyBuf.readBytes(bodyArray);
        bodyReader = new ProtoExReader(bodyArray);
        paramList = bodyReader.readMessage(List.class);
        assertTrue(paramList.isEmpty());
    }

}