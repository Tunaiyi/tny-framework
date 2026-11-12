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

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * default-to-mac-generation 1.1：算法代次默认装配契约（net-protocol"校验与加密算法代次的默认装配"）。
 * 走真实 prepareStart→UnitLoader 解析路径：默认落认证代次 (a)、逃生舱显式点名 (b)、快筛点名锚 (b2，
 * crc32CodecVerifier 装配洞回归)、未启用零影响 (c)。
 * (a) 组在默认翻转前必红、翻转后必绿；(b)(c) 全程绿（回归护栏）。fail-fast 由既有 NetPacketCodecConfigGuardTest 认领。
 */
class MacGenerationDefaultTest {

    private static final String BODY = "default-assembly-payload-0123456789-abcdefgh";

    // 单元名以生产同一推导（首字母小写）生成，杜绝字面量手误漂移（红基线实证 cRC64 全名）
    private static final String MAC_VERIFIER_UNIT  = com.tny.game.common.lifecycle.unit.UnitNames.lowerCamelName(SipHash24CodecVerifier.class);
    private static final String TILE_CRYPTO_UNIT   = com.tny.game.common.lifecycle.unit.UnitNames.lowerCamelName(XorTileCodecCrypto.class);
    private static final String CRC64_UNIT         = com.tny.game.common.lifecycle.unit.UnitNames.lowerCamelName(CRC64CodecVerifier.class);
    private static final String XOR_LEGACY_UNIT    = com.tny.game.common.lifecycle.unit.UnitNames.lowerCamelName(XOrCodecCrypto.class);
    private static final String CRC32_UNIT         = com.tny.game.common.lifecycle.unit.UnitNames.lowerCamelName(Crc32CodecVerifier.class);

    @BeforeAll
    static void registerUnits() {
        UnitLoader.register(MAC_VERIFIER_UNIT, new SipHash24CodecVerifier());
        UnitLoader.register(TILE_CRYPTO_UNIT, new XorTileCodecCrypto());
        UnitLoader.register(CRC64_UNIT, new CRC64CodecVerifier());
        UnitLoader.register(XOR_LEGACY_UNIT, new XOrCodecCrypto());
        UnitLoader.register(CRC32_UNIT, new Crc32CodecVerifier());
        UnitLoader.register("bench-default-header", new DefaultMessageHeaderCodec());
        UnitLoader.register("bench-utf8-body", new MessageBodyCodec<String>() {
            @Override
            public String decode(ByteBuf buffer) {
                byte[] bytes = new byte[buffer.readableBytes()];
                buffer.getBytes(buffer.readerIndex(), bytes);
                return new String(bytes, StandardCharsets.UTF_8);
            }

            @Override
            public void encode(String object, ByteBuf code) {
                code.writeBytes(object.getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    private static NetPacketCodecSetting newEnabledSetting() {
        NetPacketCodecSetting setting = new NetPacketCodecSetting();
        setting.setVerifyEnable(true);
        setting.setEncryptEnable(true);
        setting.setWasteBytesEnable(false);
        setting.setSecurityKeys(new String[]{"bench-default-gen-key"});
        setting.setMessageBodyCodec("bench-utf8-body");
        setting.setMessageHeaderCodec("bench-default-header");
        return setting;
    }

    /** 装配后的编码器：prepareStart 真实解析路径（管线上下文经 encodeObject 参数传入） */
    private static NetPacketV1Encoder preparedEncoder(NetPacketCodecSetting setting) {
        NetPacketV1Encoder encoder = new NetPacketV1Encoder(setting);
        encoder.prepareStart();
        return encoder;
    }

    private static Message message() {
        return new CommonMessageFactory().create(1L, MessageContents.push(Protocols.protocol(1000), BODY));
    }

    private static byte[] encode(NetPacketV1Encoder encoder, Message shared) throws Exception {
        ByteBuf out = Unpooled.buffer(64 * 1024);
        encoder.encodeObject(fieldCtx(encoder), shared, out);
        byte[] frame = new byte[out.readableBytes()];
        out.getBytes(out.readerIndex(), frame);
        out.release();
        return frame;
    }

    /** prepareStart 装配后的 ctx 注入面：encoder 持有的管线上下文经反射不可达——改为 encodeObject 直传 */
    private static ChannelHandlerContext fieldCtx(NetPacketV1Encoder encoder) {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        when(ctx.channel()).thenReturn(new EmbeddedChannel());
        NetTunnel tunnel = mock(NetTunnel.class);
        when(tunnel.getAccessId()).thenReturn(999999L);
        when(tunnel.getMessageFactory()).thenReturn(new CommonMessageFactory());
        ctx.channel().attr(NettyNetAttrKeys.TUNNEL).set(tunnel);
        return ctx;
    }

    /** (a1) 默认 unit 名字符串 = 认证代次（防手误漂移的源真理断言） */
    @Test
    void defaultsPointToMacGeneration() {
        NetPacketCodecSetting setting = new NetPacketCodecSetting();
        assertEquals(MAC_VERIFIER_UNIT, setting.getVerifier(), "verifier 默认必须是键控认证代次");
        assertEquals(TILE_CRYPTO_UNIT, setting.getCrypto(), "crypto 默认必须是键流等价快速引擎");
    }

    /** (a2) 真实装配路径：enable 且未点名 → prepareStart 解析出认证代次实例 */
    @Test
    void prepareStartResolvesMacGenerationByDefault() {
        NetPacketV1Encoder encoder = new NetPacketV1Encoder(newEnabledSetting());
        encoder.prepareStart();
        assertInstanceOf(SipHash24CodecVerifier.class, encoder.verifier);
        assertInstanceOf(XorTileCodecCrypto.class, encoder.crypto);
    }

    /** (a3) 默认装配的帧字节 == 显式认证对帧字节，且 != 显式 legacy 对帧字节 */
    @Test
    void defaultFramesMatchMacPairDifferFromLegacy() throws Exception {
        Message shared = message();
        byte[] byDefault = encode(preparedEncoder(newEnabledSetting()), shared);
        byte[] byExplicitMac = encode(preparedEncoder(newEnabledSetting()
                .setVerifier(MAC_VERIFIER_UNIT).setCrypto(TILE_CRYPTO_UNIT)), shared);
        byte[] byLegacy = encode(preparedEncoder(newEnabledSetting()
                .setVerifier(CRC64_UNIT).setCrypto(XOR_LEGACY_UNIT)), shared);

        assertArrayEquals(byExplicitMac, byDefault, "默认装配必须与显式认证对逐字节一致");
        assertFalse(Arrays.equals(byLegacy, byDefault), "默认装配必须已脱离 legacy 帧语义");
    }

    /** (b) 逃生舱：显式点名 legacy 对 → 解析回旧实例（对端未升级的过渡通道） */
    @Test
    void explicitLegacyNamesStillResolve() {
        NetPacketV1Encoder encoder = new NetPacketV1Encoder(newEnabledSetting()
                .setVerifier(CRC64_UNIT).setCrypto(XOR_LEGACY_UNIT));
        encoder.prepareStart();
        assertInstanceOf(CRC64CodecVerifier.class, encoder.verifier);
        assertInstanceOf(XOrCodecCrypto.class, encoder.crypto);
    }

    /** (b2) 快筛代次点名锚（crc32CodecVerifier Spring bean 装配洞的回归护栏）：显式点名 → prepareStart 真实 checkUnit 解析出实例 */
    @Test
    void explicitCrc32NameStillResolve() {
        NetPacketV1Encoder encoder = new NetPacketV1Encoder(newEnabledSetting()
                .setVerifier(CRC32_UNIT));
        encoder.prepareStart();
        assertInstanceOf(Crc32CodecVerifier.class, encoder.verifier);
    }

    /** (c) 未启用校验与加密：帧不携带校验尾，且与 legacy/认证装配无关（默认翻转零可观察） */
    @Test
    void disabledPathUnaffectedByDefaultFlip() throws Exception {
        Message shared = message();
        NetPacketCodecSetting off = newEnabledSetting();
        off.setVerifyEnable(false);
        off.setEncryptEnable(false);
        byte[] frameMacDefault = encode(preparedEncoder(off), shared);
        NetPacketCodecSetting offNamed = newEnabledSetting();
        offNamed.setVerifyEnable(false);
        offNamed.setEncryptEnable(false);
        offNamed.setVerifier(CRC64_UNIT);
        offNamed.setCrypto(XOR_LEGACY_UNIT);
        byte[] frameLegacyNamed = encode(preparedEncoder(offNamed), shared);
        assertArrayEquals(frameLegacyNamed, frameMacDefault, "未启用开关时默认代次不可观察");
        byte option = frameMacDefault[4];
        assertFalse(CodecConstants.isOption(option, CodecConstants.DATA_PACK_OPTION_VERIFY));
        assertFalse(CodecConstants.isOption(option, CodecConstants.DATA_PACK_OPTION_ENCRYPT));
    }
}
