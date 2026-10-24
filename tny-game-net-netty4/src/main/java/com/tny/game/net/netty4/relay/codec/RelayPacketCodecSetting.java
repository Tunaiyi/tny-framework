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

package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.netty4.network.codec.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.lifecycle.unit.UnitNames.*;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class RelayPacketCodecSetting {

    // 消息体编码器
    private String messageBodyCodec;

    // 消息头编码器
    private String messageHeaderCodec = defaultName(MessageHeaderCodec.class);

    // 错误时候是否关闭
    private boolean closeOnError = false;

    // 消息转发策略
    private String messageRelayStrategy = null;

    public RelayPacketCodecSetting() {
    }

    public RelayPacketCodecSetting(boolean closeOnError) {
        this.closeOnError = closeOnError;
    }

    public boolean isHasRelayStrategy() {
        return StringUtils.isNoneBlank(messageRelayStrategy);
    }

    public String getMessageRelayStrategy() {
        return messageRelayStrategy;
    }

    public RelayPacketCodecSetting setMessageRelayStrategy(String messageRelayStrategy) {
        this.messageRelayStrategy = messageRelayStrategy;
        return this;
    }

    public String getMessageBodyCodec() {
        return messageBodyCodec;
    }

    public String getMessageHeaderCodec() {
        return messageHeaderCodec;
    }

    public RelayPacketCodecSetting setMessageBodyCodec(String messageBodyCodec) {
        this.messageBodyCodec = messageBodyCodec;
        return this;
    }

    public RelayPacketCodecSetting setMessageHeaderCodec(String messageHeaderCodec) {
        this.messageHeaderCodec = messageHeaderCodec;
        return this;
    }

    public boolean isCloseOnError() {
        return closeOnError;
    }

    public RelayPacketCodecSetting setCloseOnError(boolean closeOnError) {
        this.closeOnError = closeOnError;
        return this;
    }

}
