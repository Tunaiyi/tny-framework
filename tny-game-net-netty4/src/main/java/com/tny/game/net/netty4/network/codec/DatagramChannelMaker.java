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
