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
package com.tny.game.net.netty4.network;

import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.codec.*;

import static com.tny.game.common.lifecycle.unit.UnitNames.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 7:41 下午
 */
public class NettyChannelSetting {

    private NettyChannelMakerSetting maker;

    private NetPacketCodecSetting encoder;

    private NetPacketCodecSetting decoder;

    private String messageHandlerFactory = lowerCamelName(DefaultMessageHandlerFactory.class);

    private String tunnelFactory = defaultName(NettyTunnelFactory.class);

    public NettyChannelSetting(String encodeBodyCodec, String decodeBodyCodec) {
        this.maker = new NettyChannelMakerSetting(DefaultDatagramChannelMaker.class);
        this.encoder = new NetPacketCodecSetting()
                .setMessageBodyCodec(encodeBodyCodec)
                .setCloseOnError(false);
        this.decoder = new NetPacketCodecSetting()
                .setMessageBodyCodec(decodeBodyCodec)
                .setCloseOnError(true);
    }

    public NettyChannelMakerSetting getMaker() {
        return this.maker;
    }

    public NetPacketCodecSetting getEncoder() {
        return encoder;
    }

    public NetPacketCodecSetting getDecoder() {
        return decoder;
    }

    public String getMessageHandlerFactory() {
        return messageHandlerFactory;
    }

    public String getTunnelFactory() {
        return tunnelFactory;
    }

    public NettyChannelSetting setMaker(NettyChannelMakerSetting maker) {
        this.maker = maker;
        return this;
    }

    public NettyChannelSetting setEncoder(NetPacketCodecSetting encoder) {
        this.encoder = encoder;
        return this;
    }

    public NettyChannelSetting setDecoder(NetPacketCodecSetting decoder) {
        this.decoder = decoder;
        return this;
    }

    public NettyChannelSetting setMessageHandlerFactory(String messageHandlerFactory) {
        this.messageHandlerFactory = messageHandlerFactory;
        return this;
    }

    public NettyChannelSetting setTunnelFactory(String tunnelFactory) {
        this.tunnelFactory = tunnelFactory;
        return this;
    }

}
