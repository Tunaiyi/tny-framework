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

package com.tny.game.net.netty4.relay.guide;

import com.tny.game.net.message.codec.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.netty4.relay.codec.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.lifecycle.unit.UnitNames.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 4:12 下午
 */
public class SpringNettyRelayChannelSetting extends NettyRelayChannelSetting {

    @NestedConfigurationProperty
    private NettyChannelMakerSetting maker;

    @NestedConfigurationProperty
    private RelayPacketCodecSetting encoder;

    @NestedConfigurationProperty
    private RelayPacketCodecSetting decoder;

    public SpringNettyRelayChannelSetting() {
        this.setMaker(new NettyChannelMakerSetting(DefaultRelayChannelMaker.class))
                .setEncoder(new RelayPacketCodecSetting()
                        .setMessageBodyCodec(lowerCamelName(ProtoExMessageBodyCodec.class))
                        .setCloseOnError(false))
                .setDecoder(new RelayPacketCodecSetting()
                        .setMessageBodyCodec(lowerCamelName(ProtoExMessageBodyCodec.class))
                        .setCloseOnError(true));
    }

    @Override
    public NettyChannelMakerSetting getMaker() {
        return as(super.getMaker());
    }

    @Override
    public NettyRelayChannelSetting setMaker(NettyChannelMakerSetting maker) {
        super.setMaker(maker);
        return this;
    }

    @Override
    public RelayPacketCodecSetting getEncoder() {
        return super.getEncoder();
    }

    @Override
    public NettyRelayChannelSetting setEncoder(RelayPacketCodecSetting encoder) {
        return super.setEncoder(encoder);
    }

    @Override
    public RelayPacketCodecSetting getDecoder() {
        return super.getDecoder();
    }

    @Override
    public NettyRelayChannelSetting setDecoder(RelayPacketCodecSetting decoder) {
        return super.setDecoder(decoder);
    }

}
