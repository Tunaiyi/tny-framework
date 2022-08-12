/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.network;

import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.codec.*;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

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

    private String messageHandler = defaultName(NettyMessageHandler.class);

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

    public String getMessageHandler() {
        return messageHandler;
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

    public NettyChannelSetting setMessageHandler(String messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    public NettyChannelSetting setTunnelFactory(String tunnelFactory) {
        this.tunnelFactory = tunnelFactory;
        return this;
    }

}
