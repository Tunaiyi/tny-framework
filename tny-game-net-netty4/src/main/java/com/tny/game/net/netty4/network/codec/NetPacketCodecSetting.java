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

import com.tny.game.net.codec.cryptoloy.XorTileCodecCrypto;
import com.tny.game.net.codec.verifier.SipHash24CodecVerifier;

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

    // 消息体验证器：默认键控认证代次（default-to-mac-generation）；legacy 经显式点名保留为逃生舱
    private String verifier = lowerCamelName(SipHash24CodecVerifier.class);

    // 消息体加密器：默认与 legacy 键流逐字节等价的快速引擎；旧件显式点名可用
    private String crypto = lowerCamelName(XorTileCodecCrypto.class);

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
