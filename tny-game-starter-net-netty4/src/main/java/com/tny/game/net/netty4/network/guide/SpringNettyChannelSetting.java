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

package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.message.codec.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.lifecycle.unit.UnitNames.*;

public class SpringNettyChannelSetting extends NettyChannelSetting {

    @NestedConfigurationProperty
    private NettyChannelMakerSetting maker;

    @NestedConfigurationProperty
    private NetPacketCodecSetting encoder;

    @NestedConfigurationProperty
    private NetPacketCodecSetting decoder;

    public SpringNettyChannelSetting() {
        super(lowerCamelName(TypeProtobufMessageBodyCodec.class), lowerCamelName(TypeProtobufMessageBodyCodec.class));
    }

    @Override
    public NettyChannelMakerSetting getMaker() {
        return super.getMaker();
    }

    @Override
    public NetPacketCodecSetting getEncoder() {
        return super.getEncoder();
    }

    @Override
    public NetPacketCodecSetting getDecoder() {
        return super.getDecoder();
    }

    @Override
    public NettyChannelSetting setMaker(NettyChannelMakerSetting maker) {
        return super.setMaker(maker);
    }

    @Override
    public NettyChannelSetting setEncoder(NetPacketCodecSetting encoder) {
        return super.setEncoder(encoder);
    }

    @Override
    public NettyChannelSetting setDecoder(NetPacketCodecSetting decoder) {
        return super.setDecoder(decoder);
    }

}
