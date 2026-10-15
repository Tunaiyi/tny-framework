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
