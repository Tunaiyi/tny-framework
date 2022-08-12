/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.netty4.channel.*;
import io.netty.channel.*;

public abstract class DatagramChannelMaker<C extends Channel> extends BaseChannelMaker<C> {

    private NetPacketEncoder encoder;

    private boolean closeOnEncodeError;

    private NetPacketDecoder decoder;

    private boolean closeOnDecodeError;

    protected DatagramChannelMaker() {
    }

    public DatagramChannelMaker(NetPacketEncoder encoder, NetPacketDecoder decoder) {
        super();
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    protected void makeChannel(C channel) {
        ChannelPipeline channelPipeline = channel.pipeline();
        channelPipeline.addLast("frameDecoder", new NetPacketDecodeHandler(this.decoder, closeOnDecodeError));
        channelPipeline.addLast("encoder", new NetPacketEncodeHandler(this.encoder, closeOnEncodeError));
    }

    @Override
    protected void postInitChannel(C channel) {
    }

    public DatagramChannelMaker<C> setEncoder(NetPacketEncoder encoder) {
        this.encoder = encoder;
        return this;
    }

    public DatagramChannelMaker<C> setDecoder(NetPacketDecoder decoder) {
        this.decoder = decoder;
        return this;
    }

    public DatagramChannelMaker<C> setCloseOnEncodeError(boolean closeOnEncodeError) {
        this.closeOnEncodeError = closeOnEncodeError;
        return this;
    }

    public DatagramChannelMaker<C> setCloseOnDecodeError(boolean closeOnDecodeError) {
        this.closeOnDecodeError = closeOnDecodeError;
        return this;
    }

}
