/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay;

import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.relay.codec.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 2:54 下午
 */
public class NettyRelayChannelSetting {

    private NettyChannelMakerSetting maker = new NettyChannelMakerSetting(DefaultRelayChannelMaker.class);

    private RelayPacketCodecSetting encoder = new RelayPacketCodecSetting(false);

    private RelayPacketCodecSetting decoder = new RelayPacketCodecSetting(true);

    public NettyChannelMakerSetting getMaker() {
        return maker;
    }

    public RelayPacketCodecSetting getEncoder() {
        return encoder;
    }

    public RelayPacketCodecSetting getDecoder() {
        return decoder;
    }

    public NettyRelayChannelSetting setMaker(NettyChannelMakerSetting maker) {
        this.maker = maker;
        return this;
    }

    public NettyRelayChannelSetting setEncoder(RelayPacketCodecSetting encoder) {
        this.encoder = encoder;
        return this;
    }

    public NettyRelayChannelSetting setDecoder(RelayPacketCodecSetting decoder) {
        this.decoder = decoder;
        return this;
    }

}
