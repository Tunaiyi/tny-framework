/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.netty4.network.codec.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

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
