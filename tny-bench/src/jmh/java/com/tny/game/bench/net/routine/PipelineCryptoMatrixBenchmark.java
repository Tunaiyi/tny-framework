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
package com.tny.game.bench.net.routine;

import com.tny.game.bench.net.shared.AeadRfc7539;
import com.tny.game.bench.net.shared.Crc64Slicing;
import com.tny.game.bench.net.shared.SipHash64;
import com.tny.game.common.digest.binary.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.openjdk.jmh.annotations.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32C;

/**
 * 校验/加密算法在**生产编解码全管线**上的吞吐矩阵：装配复刻 PacketCodecBenchmark
 * （真实 NetPacketV1Encoder/Decoder + EmbeddedChannel context，零 mock），
 * 候选实现以 CodecVerifier/CodecCrypto 插件注入——数字可直接对照 baseline 的
 * verify=false 2.05M / verify=true(仅CRC) 0.97M 锚点；"legacy" 组合是
 * 生产默认配置（verify+encrypt 同开）的首次量化。
 * CLI：-f 2 -wi 3 -i 5 -w 500ms -r 1s（与 baseline 同参）。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class PipelineCryptoMatrixBenchmark {

    /**
     * none=双关锚点；legacy=生产默认(CRC64逐字节+重复键XOR)；
     * byteNoAlloc_*=wire 兼容 drop-in 候选（复用生产 CRC64 静态方法，仅去每包分配）；
     * slicing_xorTile=标准 CRC64 假设上界（算术右移语义不兼容现网，非 drop-in）；
     * crc32c_*=硬件 CRC32C（4B 校验码，协议变更）。
     */
    @Param({"none", "legacy", "only_xor", "only_crc64", "byteNoAlloc_xorTile", "slicing_xorTile", "crc32c_xorTile",
            "byteNoAlloc_chacha", "crc32c_chacha", "noise_aead",
            "siphash_xorTile", "siphash_chacha",
            "prod_crc64_xortile", "prod_siphash24_xortile", "prod_crc32_xortile",
            "prod_siphash24_xorprod", "prod_crc32_xorprod", "prod_siphash24_chacha", "legacy_v1"})
    private String algo;

    /** 键形窗口（审计修正）：9B=计数器降级路径；16B=字级对齐路径。管线排行结论必须按键形分列。 */
    @Param({"bench-key", "0123456789abcdef"})
    private String benchKey;

    /** 消息体字节数（尺寸维，E1）：42→帧约 96B（历史口径）；994→帧约 1KB（状态快照类大 body 外推终点）。 */
    @Param({"42", "994"})
    private int size;

    private NetPacketV1Encoder encoder;
    private NetPacketV1Decoder decoder;
    private EmbeddedChannel channel;
    private PacketCodecBenchmark.BenchTunnel stubTunnel;
    private ChannelHandlerContext ctx;
    private NetPacketDecodeMarker marker;
    private Message message;
    private ByteBuf out;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        NetPacketCodecSetting config = new NetPacketCodecSetting();
        config.setWasteBytesEnable(false);
        config.setSecurityKeys(new String[]{benchKey});
        CodecVerifier codecVerifier;
        CodecCrypto codecCrypto;
        switch (algo) {
            case "none": {
                config.setVerifyEnable(false);
                config.setEncryptEnable(false);
                codecVerifier = new NoopCodecVerifier();
                codecCrypto = new NoneCodecCrypto();
                break;
            }
            case "legacy": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new CRC64CodecVerifier();
                codecCrypto = new XOrCodecCrypto();
                break;
            }
            case "byteNoAlloc_xorTile": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new ByteNoAllocCodecVerifier();
                codecCrypto = new XorTileProtoCodecCrypto();
                break;
            }
            case "slicing_xorTile": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new SlicingBy8CodecVerifier();
                codecCrypto = new XorTileProtoCodecCrypto();
                break;
            }
            case "crc32c_xorTile": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new Crc32cCodecVerifier();
                codecCrypto = new XorTileProtoCodecCrypto();
                break;
            }
            case "byteNoAlloc_chacha": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new ByteNoAllocCodecVerifier();
                codecCrypto = new ChaCha20CodecCrypto();
                break;
            }
            case "crc32c_chacha": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new Crc32cCodecVerifier();
                codecCrypto = new ChaCha20CodecCrypto();
                break;
            }
            case "siphash_xorTile": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new SipHashCodecVerifier();
                codecCrypto = new XorTileProtoCodecCrypto();
                break;
            }
            case "siphash_chacha": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new SipHashCodecVerifier();
                codecCrypto = new ChaCha20CodecCrypto();
                break;
            }
            case "prod_siphash24_xorprod": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new SipHash24CodecVerifier();
                codecCrypto = new XOrCodecCrypto();
                break;
            }
            case "prod_crc32_xorprod": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new Crc32CodecVerifier();
                codecCrypto = new XOrCodecCrypto();
                break;
            }
            case "legacy_v1": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new VarargsCrc64ProtoCodecVerifier();
                codecCrypto = new ModXorProtoCodecCrypto();
                break;
            }
            case "prod_crc64_xortile": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new CRC64CodecVerifier();
                codecCrypto = new XorTileCodecCrypto();
                break;
            }
            case "prod_siphash24_chacha": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new SipHash24CodecVerifier();
                codecCrypto = new ChaCha20CodecCrypto();
                break;
            }
            case "only_xor": {
                config.setVerifyEnable(false);
                config.setEncryptEnable(true);
                codecVerifier = new NoopCodecVerifier();
                codecCrypto = new XOrCodecCrypto();
                break;
            }
            case "only_crc64": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(false);
                codecVerifier = new CRC64CodecVerifier();
                codecCrypto = new NoneCodecCrypto();
                break;
            }
            case "prod_crc32_xortile": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new Crc32CodecVerifier();
                codecCrypto = new XorTileCodecCrypto();
                break;
            }
            case "prod_siphash24_xortile": {
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                codecVerifier = new SipHash24CodecVerifier();
                codecCrypto = new XorTileCodecCrypto();
                break;
            }
            case "noise_aead": {
                AeadRfc7539.selfTest();
                config.setVerifyEnable(true);
                config.setEncryptEnable(true);
                NoiseAeadSecurity security = new NoiseAeadSecurity();
                codecVerifier = security;
                codecCrypto = security;
                break;
            }
            default:
                throw new IllegalArgumentException("unknown algo " + algo);
        }

        NettyMessageCodec messageCodec = new DefaultNettyMessageCodec(new MessageBodyCodec<String>() {
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
        }, new DefaultMessageHeaderCodec());

        channel = new EmbeddedChannel(new io.netty.channel.ChannelDuplexHandler() {
        });
        stubTunnel = new PacketCodecBenchmark.BenchTunnel();
        channel.attr(NettyNetAttrKeys.TUNNEL).set(stubTunnel);
        ctx = channel.pipeline().firstContext();

        encoder = new NetPacketV1Encoder(config);
        encoder.setMessageCodec(messageCodec);
        inject(encoder, codecVerifier, codecCrypto);

        decoder = new NetPacketV1Decoder(config);
        decoder.setMessageCodec(messageCodec);
        inject(decoder, codecVerifier, codecCrypto);

        marker = new NetPacketDecodeMarker();
        message = new CommonMessageFactory().create(1L,
                MessageContents.push(Protocols.protocol(1000), "b".repeat(size)));
        out = Unpooled.buffer(4096);

        // 装配自检：候选插件必须在真实管线上往返成功且体内容还原一致，否则数字无意义
        out.clear();
        encoder.encodeObject(ctx, message, out);
        Message decoded = decoder.decodeObject(ctx, out, marker);
        Object body = decoded == null ? null : decoded.getBody();
        if (!(body instanceof String) || !body.equals("b".repeat(size))) {
            throw new AssertionError("pipeline roundtrip failed for algo " + algo + ", body=" + body);
        }
    }

    private static void inject(NetPacketV1Codec codec, CodecVerifier verifier, CodecCrypto crypto) throws Exception {
        java.lang.reflect.Field vf = NetPacketV1Codec.class.getDeclaredField("verifier");
        vf.setAccessible(true);
        vf.set(codec, verifier);
        java.lang.reflect.Field cf = NetPacketV1Codec.class.getDeclaredField("crypto");
        cf.setAccessible(true);
        cf.set(codec, crypto);
    }

    @Benchmark
    public Object encodeThenDecode() throws Exception {
        out.clear();
        encoder.encodeObject(ctx, message, out);
        return decoder.decodeObject(ctx, out, marker);
    }

    // ==================== 候选插件（基准专用原型，验证通过后方可转正） ====================

    /** slicing-by-8 CRC64：与生产同多项式同混入序列，校验码仍 8B（帧格式零变化） */
    static final class SlicingBy8CodecVerifier implements CodecVerifier {
        private final Crc64Slicing slicing = new Crc64Slicing();

        @Override
        public int getCodeLength() {
            return 8;
        }

        @Override
        public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
            int number = packager.getPacketNumber();
            int code = packager.getPacketCode();
            byte[] numberBytes = BytesAide.int2Bytes(number);
            byte[] codeBytes = BytesAide.int2Bytes(code);
            long crc = Crc64Slicing.CRC64_INITIAL;
            crc = slicing.update(crc, numberBytes, 0, 4);
            crc = slicing.update(crc, body, offset, length);
            crc = slicing.update(crc, packager.getAccessKeyBytes(), 0, packager.getAccessKeyBytes().length);
            crc = slicing.update(crc, codeBytes, 0, 4);
            return BytesAide.long2Bytes(crc);
        }

        @Override
        public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
            return Arrays.equals(generate(packager, body, offset, length), verifyCode);
        }
    }

    /**
     * wire 兼容 drop-in：复用生产 CRC64 静态方法（算术右移语义原样），仅消除
     * doGenerate 的每包 7 对象分配（int2Bytes×2、wrap×4、long2Bytes）。
     * 校验码经复用 scratch 返回——契约：编码器 generate 后立即 writeBytes 消费（同调用栈）。
     */
    static final class ByteNoAllocCodecVerifier implements CodecVerifier {
        private static final long INITIAL = 0xFFFFFFFFFFFFFFFFL;
        private final byte[] num4 = new byte[4];
        private final byte[] code4 = new byte[4];
        private final byte[] codeScratch = new byte[8];

        @Override
        public int getCodeLength() {
            return 8;
        }

        @Override
        public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
            BytesAide.int2Bytes(packager.getPacketNumber(), num4, 0);
            BytesAide.int2Bytes(packager.getPacketCode(), code4, 0);
            byte[] accessKey = packager.getAccessKeyBytes();
            long crc = CRC64.crc64Long(INITIAL, num4, 0, 4);
            crc = CRC64.crc64Long(crc, body, offset, length);
            crc = CRC64.crc64Long(crc, accessKey, 0, accessKey.length);
            crc = CRC64.crc64Long(crc, code4, 0, 4);
            return BytesAide.long2Bytes(crc, codeScratch, 0);
        }

        @Override
        public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
            generate(packager, body, offset, length);
            for (int i = 0; i < 8; i++) {
                if (codeScratch[i] != verifyCode[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    /** java.util.zip.CRC32C（硬件 intrinsic）keyed 混入形态同生产；校验码 4B，帧预算再省 4B */
    static final class Crc32cCodecVerifier implements CodecVerifier {
        private final CRC32C crc32c = new CRC32C();

        @Override
        public int getCodeLength() {
            return 4;
        }

        @Override
        public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
            int number = packager.getPacketNumber();
            int code = packager.getPacketCode();
            byte[] numberBytes = BytesAide.int2Bytes(number);
            byte[] codeBytes = BytesAide.int2Bytes(code);
            crc32c.reset();
            crc32c.update(numberBytes);
            crc32c.update(body, offset, length);
            crc32c.update(packager.getAccessKeyBytes());
            crc32c.update(codeBytes);
            return BytesAide.int2Bytes((int) crc32c.getValue());
        }

        @Override
        public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
            return Arrays.equals(generate(packager, body, offset, length), verifyCode);
        }
    }

    /** 重复键 XOR 的 tile 预合并 + 8 字节对齐实现：键流与输出与 XOrCodecCrypto 逐字节等价 */
    /** optimize-legacy-codec-paths E2 配对：升级前 legacy 的逐字复刻（varargs-ByteBuffer CRC64 + 逐字节双取模 XOr）。 */
    static final class VarargsCrc64ProtoCodecVerifier implements CodecVerifier {
        @Override public int getCodeLength() { return 8; }
        @Override public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
            return BytesAide.long2Bytes(CRC64.crc64Long(
                    ByteBuffer.wrap(BytesAide.int2Bytes(packager.getPacketNumber())),
                    ByteBuffer.wrap(body, offset, length),
                    ByteBuffer.wrap(packager.getAccessKeyBytes()),
                    ByteBuffer.wrap(BytesAide.int2Bytes(packager.getPacketCode()))));
        }
        @Override public boolean verify(DataPackageContext p, byte[] b, int o, int l, byte[] code) {
            return Arrays.equals(generate(p, b, o, l), code);
        }
    }

    static final class ModXorProtoCodecCrypto implements CodecCrypto {
        @Override public byte[] encrypt(DataPackageContext c, byte[] b, int o, int l) {
            return BytesAide.xor(b, o, l, c.getPackSecurityKey(), BytesAide.int2Bytes(c.getPacketCode()));
        }
        @Override public byte[] decrypt(DataPackageContext c, byte[] b, int o, int l) { return encrypt(c, b, o, l); }
    }

    /** 原型件（命名遮蔽修正）：矩阵内联 counter-tile 实现，区别于生产 unit。 */
    static final class XorTileProtoCodecCrypto implements CodecCrypto {

        @Override
        public byte[] encrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
            return xor(context, bytes, offset, length);
        }

        @Override
        public byte[] decrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
            return xor(context, bytes, offset, length);
        }

        private byte[] xor(DataPackageContext context, byte[] bytes, int offset, int length) {
            byte[] security = context.getPackSecurityKey();
            byte[] code = BytesAide.int2Bytes(context.getPacketCode());
            int period = lcm(security.length, code.length);
            if ((period & 7) != 0 || period > 64 || length < 16) {
                // 非 8 倍周期或短包：退回逐字节（正确性优先）
                byte[] tile = new byte[period];
                for (int i = 0; i < period; i++) {
                    tile[i] = (byte) (security[i % security.length] ^ code[i % code.length]);
                }
                for (int i = offset, n = 0; n < length; i++, n++) {
                    bytes[i] ^= tile[n % period];
                }
                return bytes;
            }
            byte[] tile = new byte[period];
            for (int i = 0; i < period; i++) {
                tile[i] = (byte) (security[i % security.length] ^ code[i % code.length]);
            }
            long[] tileLongs = new long[period >>> 3];
            for (int k = 0; k < tileLongs.length; k++) {
                tileLongs[k] = le64(tile, k << 3);
            }
            int i = offset;
            int n = 0;
            for (; n + 8 <= length; i += 8, n += 8) {
                long v = le64(bytes, i) ^ tileLongs[(n >>> 3) % tileLongs.length];
                bytes[i] = (byte) v;
                bytes[i + 1] = (byte) (v >>> 8);
                bytes[i + 2] = (byte) (v >>> 16);
                bytes[i + 3] = (byte) (v >>> 24);
                bytes[i + 4] = (byte) (v >>> 32);
                bytes[i + 5] = (byte) (v >>> 40);
                bytes[i + 6] = (byte) (v >>> 48);
                bytes[i + 7] = (byte) (v >>> 56);
            }
            for (; n < length; i++, n++) {
                bytes[i] ^= tile[n % period];
            }
            return bytes;
        }

        private static long le64(byte[] b, int i) {
            return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                    | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
        }

        private static int lcm(int a, int b) {
            int g = a;
            int x = b;
            while (x != 0) { int t = g % x; g = x; x = t; }
            return a / g * b;
        }
    }

    /** JCE ChaCha20 密钥流：IV 由包号+包码派生（两端 context 同步，解码侧可复原） */
    static final class ChaCha20CodecCrypto implements CodecCrypto {
        private static final byte[] IV_SALT = {0x7d, (byte) 0x9f, 0x33, 0x51};
        /** 生产形态同理：一条连接的加密侧与解密侧是两个独立 Cipher 实例 */
        private final Cipher[] ciphers = new Cipher[2];
        private SecretKeySpec keySpec;
        private byte[] ctScratch = new byte[8192];
        /** ChaCha20ParameterSpec nonce 形状：JDK19+ 要求 12B，JDK11~18 要求 8B+counter；构造器即抛 IAE 可探测 */
        private int nonceMode = 0;

        @Override
        public byte[] encrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
            return transform(Cipher.ENCRYPT_MODE, context, bytes, offset, length);
        }

        @Override
        public byte[] decrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
            return transform(Cipher.DECRYPT_MODE, context, bytes, offset, length);
        }

        private byte[] transform(int mode, DataPackageContext context, byte[] bytes, int offset, int length) {
            try {
                Cipher cipher = cipherFor(mode);
                byte[] iv = new byte[12];
                BytesAide.int2Bytes(context.getPacketNumber(), iv, 0);
                BytesAide.int2Bytes(context.getPacketCode(), iv, 4);
                System.arraycopy(IV_SALT, 0, iv, 8, 4);
                if (length > ctScratch.length) {
                    ctScratch = new byte[length + 1024];
                }
                int code = context.getPacketCode();
                boolean probed = false;
                if (nonceMode == 0) {
                    try {
                        cipher.init(mode, keySpec, new ChaCha20ParameterSpec(iv, 0));
                        nonceMode = 12;
                        probed = true;
                    } catch (IllegalArgumentException legacySpec) {
                        nonceMode = 8;
                    }
                }
                if (!probed) {
                    if (nonceMode == 12) {
                        cipher.init(mode, keySpec, new ChaCha20ParameterSpec(iv, 0));
                    } else {
                        cipher.init(mode, keySpec, new ChaCha20ParameterSpec(Arrays.copyOf(iv, 8), code));
                    }
                }
                cipher.doFinal(bytes, offset, length, ctScratch, 0);
                System.arraycopy(ctScratch, 0, bytes, offset, length);
                return bytes;
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException("chacha20 crypto failed", e);
            }
        }

        private Cipher cipherFor(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException {
            int index = mode == Cipher.ENCRYPT_MODE ? 0 : 1;
            Cipher cipher = ciphers[index];
            if (cipher == null) {
                if (keySpec == null) {
                    // JDK 要求 ChaCha20 密钥 256-bit（部分版本允许 128，此处按最严格口径）
                    byte[] raw = "bench-chacha-key".getBytes(StandardCharsets.UTF_8);
                    keySpec = new SecretKeySpec(Arrays.copyOf(raw, 32), "ChaCha20");
                }
                cipher = Cipher.getInstance("ChaCha20");
                ciphers[index] = cipher;
            }
            return cipher;
        }
    }

    /**
     * 会话密钥档的管线插件（基准内用静态"会话"密钥代替握手派生）：
     * SipHash-2-4(body) over 域分离密钥（number/code 折入 key tweak），校验码 8B——与现 CRC64 等长。
     * 这是"MAC 主力防伪 + 廉价加密混淆"平衡档的完整性件。
     */
    static final class SipHashCodecVerifier implements CodecVerifier {
        private static final long K0 = 0x0706050403020100L;
        private static final long K1 = 0x0f0e0d0c0b0a0908L;
        private final byte[] out8 = new byte[8];

        @Override
        public int getCodeLength() {
            return 8;
        }

        @Override
        public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
            long k0 = K0 ^ Integer.rotateLeft(packager.getPacketNumber(), 21);
            long k1 = K1 ^ Long.rotateLeft(packager.getPacketCode() & 0xffffffffL, 17)
                    ^ packager.getAccessKeyBytes()[0];
            long mac = SipHash64.sipHash64(k0, k1, body, offset, length);
            BytesAide.long2Bytes(mac, out8, 0);
            return out8;
        }

        @Override
        public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
            generate(packager, body, offset, length);
            int diff = 0;
            for (int i = 0; i < 8; i++) {
                diff |= out8[i] ^ verifyCode[i];
            }
            return diff == 0;
        }
    }

    /**
     * Noise 传输层形态的管线插件：一个对象同时占 verifier+crypto 两个接缝（AEAD 代次下二者本为一体），
     * 持久 {@link AeadRfc7539} 状态 + 计数器 nonce（Noise 约定 [4B零‖8B LE]），number 进 AAD。
     * 适配管线既有时序：编码侧 generate(密封并暂存密文、返回 tag) → encrypt(回填密文)；
     * 解码侧 decrypt(暂存密文、原地解密) → verify(对暂存密文重算 tag 比对)。
     * 注：基准是 EmbeddedChannel **环回**模型——同一条消息即出又入，故用单密钥单计数器
     * （真实连接为双向双密钥，即本设计 securityState 槽位的读写各一）。
     */
    static final class NoiseAeadSecurity implements CodecVerifier, CodecCrypto {
        private final AeadRfc7539 aead;
        private long counter;
        private final byte[] aadNum = new byte[4];
        private byte[] pendingCt = new byte[8192];

        NoiseAeadSecurity() {
            byte[] key = new byte[32];
            for (int i = 0; i < 32; i++) {
                key[i] = (byte) (i * 7 + 13);
            }
            aead = new AeadRfc7539(key);
        }

        private void setNonce(long counter) {
            byte[] nonce = new byte[12];
            for (int k = 0; k < 8; k++) {
                nonce[4 + k] = (byte) (counter >>> (8 * k));
            }
            aead.setNonce(nonce);
        }

        @Override
        public int getCodeLength() {
            return 16;
        }

        @Override
        public byte[] generate(DataPackageContext packager, byte[] body, int offset, int length) {
            if (length > pendingCt.length) {
                pendingCt = new byte[length + 4096];
            }
            BytesAide.int2Bytes(packager.getPacketNumber(), aadNum, 0);
            setNonce(++counter);
            aead.seal(body, offset, length, aadNum, 0, 4, pendingCt);
            return aead.tag();
        }

        @Override
        public boolean verify(DataPackageContext packager, byte[] body, int offset, int length, byte[] verifyCode) {
            BytesAide.int2Bytes(packager.getPacketNumber(), aadNum, 0);
            aead.auth(pendingCt, length, aadNum, 0, 4);
            byte[] t = aead.tag();
            int diff = 0;
            for (int i = 0; i < 16; i++) {
                diff |= t[i] ^ verifyCode[i];
            }
            return diff == 0;
        }

        @Override
        public byte[] encrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
            System.arraycopy(pendingCt, 0, bytes, offset, length);
            return bytes;
        }

        @Override
        public byte[] decrypt(DataPackageContext context, byte[] bytes, int offset, int length) {
            // 环回模型：nonce 已由本轮 generate 设定且实例保留；ct 已在 pendingCt（与 generate 同源）
            aead.decryptInPlace(bytes, offset, length);
            return bytes;
        }
    }

}
