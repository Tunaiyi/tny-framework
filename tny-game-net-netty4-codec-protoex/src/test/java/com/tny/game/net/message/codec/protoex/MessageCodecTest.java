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

import com.tny.game.net.codec.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.session.*;
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

    private final ProtoExMessageBodyCodec<Object> objectCodec = new ProtoExMessageBodyCodec<>();

    private final MessageHeaderCodec messageHeaderCodec = new DefaultMessageHeaderCodec();

    private final NettyMessageCodec codec = new DefaultNettyMessageCodec(
            this.objectCodec, messageHeaderCodec, MessageRelayStrategy.NO_RELAY_STRATEGY);

    private final NettyMessageCodec codecNoDecode = new DefaultNettyMessageCodec(
            this.objectCodec, messageHeaderCodec, (head) -> true);

    private final CommonMessageFactory messageFactory = new CommonMessageFactory();

    private NetMessage createMessage(Object body) {
        return this.messageFactory.create(1L, MessageContents.request(Protocols.protocol(100_100), body));
    }

    @Test
    void encodeDecode() throws Exception {
        TestMsgObject body = new TestMsgObject(100, "I am test body!");
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

        ByteBuf bodyBuf = (ByteBuf) bodyBytes.getBody();
        byte[] bodyArray = new byte[bodyBuf.readableBytes()];
        bodyBuf.readBytes(bodyArray);
        try (ProtoExReader bodyReader = new ProtoExReader(bodyArray)) {
            List<?> paramList = bodyReader.readMessage(List.class);
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
            bodyBuf = (ByteBuf) bodyBytes.getBody();
            bodyArray = new byte[bodyBuf.readableBytes()];
            bodyBuf.readBytes(bodyArray);
        }
        try (ProtoExReader bodyReader = new ProtoExReader(bodyArray)) {
            List<?> paramList = bodyReader.readMessage(List.class);
            assertTrue(paramList.isEmpty());
        }
    }

}