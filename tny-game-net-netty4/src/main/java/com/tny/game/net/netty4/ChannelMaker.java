package com.tny.game.net.netty4;

import com.tny.game.common.unit.annotation.UnitInterface;
import com.tny.game.net.netty4.codec.*;
import io.netty.channel.*;

@UnitInterface
public abstract class ChannelMaker<C extends Channel> {

    private DataPacketEncoder encoder;

    private DataPacketDecoder decoder;

    protected ChannelMaker() {
    }

    public ChannelMaker(DataPacketEncoder encoder, DataPacketDecoder decoder) {
        super();
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public void initChannel(C channel) throws Exception {
        ChannelPipeline channelPipeline = channel.pipeline();
        this.prepareAddCoder(channelPipeline);
        channelPipeline.addLast("frameDecoder", new DecoderHandler(this.decoder));
        channelPipeline.addLast("encoder", new EncodeHandler(this.encoder));
        this.postAddCoder(channelPipeline);
        this.postInitChannel(channel);
    }

    protected void postAddCoder(ChannelPipeline channelPipeline) {
    }

    protected void prepareAddCoder(ChannelPipeline channelPipeline) {
    }

    protected void postInitChannel(C channel) {
    }

    public ChannelMaker<C> setEncoder(DataPacketEncoder encoder) {
        this.encoder = encoder;
        return this;
    }

    public ChannelMaker<C> setDecoder(DataPacketDecoder decoder) {
        this.decoder = decoder;
        return this;
    }

}
