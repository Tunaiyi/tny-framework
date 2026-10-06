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
