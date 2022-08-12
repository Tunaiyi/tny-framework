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

import com.tny.game.net.netty4.channel.*;
import io.netty.channel.*;

public abstract class RelayPackChannelMaker<C extends Channel> extends BaseChannelMaker<C> {

    private RelayPacketEncoder encoder;

    private boolean closeOnEncodeError;

    private RelayPacketDecoder decoder;

    private boolean closeOnDecodeError;

    protected RelayPackChannelMaker() {
    }

    public RelayPackChannelMaker(RelayPacketEncoder encoder, RelayPacketDecoder decoder) {
        super();
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public void makeChannel(C channel) {
        ChannelPipeline channelPipeline = channel.pipeline();
        channelPipeline.addLast("frameDecoder", new RelayPackDecodeHandler(this.decoder, closeOnDecodeError));
        channelPipeline.addLast("encoder", new RelayPackEncodeHandler(this.encoder, closeOnEncodeError));
    }

    public RelayPackChannelMaker<C> setEncoder(RelayPacketEncoder encoder) {
        this.encoder = encoder;
        return this;
    }

    public RelayPackChannelMaker<C> setDecoder(RelayPacketDecoder decoder) {
        this.decoder = decoder;
        return this;
    }

    public RelayPackChannelMaker<C> setCloseOnEncodeError(boolean closeOnEncodeError) {
        this.closeOnEncodeError = closeOnEncodeError;
        return this;
    }

    public RelayPackChannelMaker<C> setCloseOnDecodeError(boolean closeOnDecodeError) {
        this.closeOnDecodeError = closeOnDecodeError;
        return this;
    }

}
