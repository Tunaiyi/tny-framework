package com.tny.game.net.codec.v1;

import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.message.codec.protoex.*;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class DataPacketV1Config {

    // 密钥
    private String[] securityKeys = new String[]{};
    // 是否加密
    private boolean encryptEnable = false;
    // 是否设置废字节
    private boolean wasteBytesEnable = false;
    // 是否校验
    private boolean verifyEnable = false;
    // 可跳过包数步长
    private long skipNumberStep = 30;
    // 包超时
    private long packetTimeout = 60000;
    // 最大废字节数
    private int maxWasteBitSize = 30;
    // 消息体编码器
    private String bodyCodec = ProtoExMessageBodyCodec.class.getSimpleName();
    //    // 消息尾编码器
    //    private String tailCodec = ProtoExMessageBodyCodec.class.getSimpleName();
    // 消息体解码测刘娥
    private String bodyDecodeStrategy = null;
    // 消息体验证器
    private String verifier = CRC64CodecVerifier.class.getSimpleName();
    // 消息体加密器
    private String crypto = XOrCodecCrypto.class.getSimpleName();
    // 默认最大包大小
    private int maxPayloadLength = 0xFFFF;

    // 密钥字节
    private volatile byte[][] securityKeysBytes;

    public DataPacketV1Config() {
    }

    public String[] getSecurityKeys() {
        return this.securityKeys;
    }

    private byte[][] securityKeysBytes() {
        if (this.securityKeysBytes != null) {
            return this.securityKeysBytes;
        }
        synchronized (this) {
            if (this.securityKeysBytes != null) {
                return this.securityKeysBytes;
            }
            byte[][] securityBytesKeys = new byte[this.securityKeys.length][];
            for (int i = 0; i < this.securityKeys.length; i++) {
                securityBytesKeys[i] = this.securityKeys[i].getBytes();
            }
            this.securityKeysBytes = securityBytesKeys;
        }
        return this.securityKeysBytes;
    }

    public byte[] getSecurityKeyBytes(int value) {
        byte[][] securityKeysBytes = securityKeysBytes();
        return securityKeysBytes[value % securityKeysBytes.length];
    }

    public String getSecurityKeys(long value) {
        if (ArrayUtils.isEmpty(this.securityKeys)) {
            return "";
        }
        return this.securityKeys[(int)(value % this.securityKeys.length)];
    }

    public long getSkipNumberStep() {
        return this.skipNumberStep;
    }

    public boolean isEncryptEnable() {
        return this.encryptEnable;
    }

    public boolean isWasteBytesEnable() {
        return this.wasteBytesEnable;
    }

    public boolean isVerifyEnable() {
        return this.verifyEnable;
    }

    public int getMaxWasteBitSize() {
        return this.maxWasteBitSize;
    }

    public long getPacketTimeout() {
        return this.packetTimeout;
    }

    public String getBodyCodec() {
        return this.bodyCodec;
    }

    //        public String getTailCodec() {
    //            return this.tailCodec;
    //        }

    public String getBodyDecodeStrategy() {
        return this.bodyDecodeStrategy;
    }

    public String getVerifier() {
        return this.verifier;
    }

    public String getCrypto() {
        return this.crypto;
    }

    public int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }

    public DataPacketV1Config setEncryptEnable(boolean encryptEnable) {
        this.encryptEnable = encryptEnable;
        return this;
    }

    public DataPacketV1Config setWasteBytesEnable(boolean wasteBytesEnable) {
        this.wasteBytesEnable = wasteBytesEnable;
        return this;
    }

    public DataPacketV1Config setVerifyEnable(boolean verifyEnable) {
        this.verifyEnable = verifyEnable;
        return this;
    }

    public DataPacketV1Config setSkipNumberStep(long skipNumberStep) {
        this.skipNumberStep = skipNumberStep;
        return this;
    }

    public DataPacketV1Config setPacketTimeout(long packetTimeout) {
        this.packetTimeout = packetTimeout;
        return this;
    }

    public DataPacketV1Config setMaxWasteBitSize(int maxWasteBitSize) {
        this.maxWasteBitSize = maxWasteBitSize;
        return this;
    }

    public DataPacketV1Config setSecurityKeys(String[] securityKeys) {
        this.securityKeys = securityKeys;
        return this;
    }

    public DataPacketV1Config setVerifier(String verifier) {
        this.verifier = verifier;
        return this;
    }

    public DataPacketV1Config setCrypto(String crypto) {
        this.crypto = crypto;
        return this;
    }

    public DataPacketV1Config setBodyCodec(String bodyCodec) {
        this.bodyCodec = bodyCodec;
        return this;
    }

    //    public DataPacketV1Config setTailCodec(String tailCodec) {
    //        this.tailCodec = tailCodec;
    //        return this;
    //    }

    public DataPacketV1Config setBodyDecodeStrategy(String bodyDecodeStrategy) {
        this.bodyDecodeStrategy = bodyDecodeStrategy;
        return this;
    }

    public DataPacketV1Config setMaxPayloadLength(int maxPayloadLength) {
        this.maxPayloadLength = maxPayloadLength;
        return this;
    }

}
