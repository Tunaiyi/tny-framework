package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class DatagramPackCodecSetting extends DataPackCodecOptions {

    // 消息体编码器
    private String messageBodyCodec = null;

    // 消息转发策略
    private String messageRelayStrategy = null;

    // 消息体验证器
    private String verifier = lowerCamelName(CRC64CodecVerifier.class);

    // 消息体加密器
    private String crypto = lowerCamelName(XOrCodecCrypto.class);

    private boolean closeOnError = false;

    public DatagramPackCodecSetting() {
    }

    public String getMessageBodyCodec() {
        return this.messageBodyCodec;
    }

    public boolean isHasMessageRelayStrategy() {
        return StringUtils.isNoneBlank(this.messageRelayStrategy);
    }

    public String getMessageRelayStrategy() {
        return this.messageRelayStrategy;
    }

    public String getVerifier() {
        return this.verifier;
    }

    public String getCrypto() {
        return this.crypto;
    }

    public boolean isCloseOnError() {
        return closeOnError;
    }

    public DatagramPackCodecSetting setVerifier(String verifier) {
        this.verifier = verifier;
        return this;
    }

    public DatagramPackCodecSetting setCrypto(String crypto) {
        this.crypto = crypto;
        return this;
    }

    public DatagramPackCodecSetting setMessageBodyCodec(String messageBodyCodec) {
        this.messageBodyCodec = messageBodyCodec;
        return this;
    }

    public DatagramPackCodecSetting setMessageRelayStrategy(String messageRelayStrategy) {
        this.messageRelayStrategy = messageRelayStrategy;
        return this;
    }

    public DatagramPackCodecSetting setCloseOnError(boolean closeOnError) {
        this.closeOnError = closeOnError;
        return this;
    }

}
