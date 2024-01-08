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

package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.lifecycle.unit.UnitNames.*;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class NetPacketCodecSetting extends DataPackCodecOptions {

    // 消息体编码器
    private String messageBodyCodec = null;

    // 消息头编码器
    private String messageHeaderCodec = defaultName(MessageHeaderCodec.class);

    // 消息转发策略
    private String messageRelayStrategy = null;

    // 消息体验证器
    private String verifier = lowerCamelName(CRC64CodecVerifier.class);

    // 消息体加密器
    private String crypto = lowerCamelName(XOrCodecCrypto.class);

    private boolean closeOnError = false;

    public NetPacketCodecSetting() {
    }

    public String getMessageBodyCodec() {
        return this.messageBodyCodec;
    }

    public String getMessageHeaderCodec() {
        return messageHeaderCodec;
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

    public NetPacketCodecSetting setVerifier(String verifier) {
        this.verifier = verifier;
        return this;
    }

    public NetPacketCodecSetting setCrypto(String crypto) {
        this.crypto = crypto;
        return this;
    }

    public NetPacketCodecSetting setMessageBodyCodec(String messageBodyCodec) {
        this.messageBodyCodec = messageBodyCodec;
        return this;
    }

    public NetPacketCodecSetting setMessageRelayStrategy(String messageRelayStrategy) {
        this.messageRelayStrategy = messageRelayStrategy;
        return this;
    }

    public NetPacketCodecSetting setCloseOnError(boolean closeOnError) {
        this.closeOnError = closeOnError;
        return this;
    }

    public NetPacketCodecSetting setMessageHeaderCodec(String messageHeaderCodec) {
        this.messageHeaderCodec = messageHeaderCodec;
        return this;
    }

}
