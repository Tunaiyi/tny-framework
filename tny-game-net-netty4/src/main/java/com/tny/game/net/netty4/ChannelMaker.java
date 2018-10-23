package com.tny.game.net.netty4;

import com.tny.game.net.netty4.codec.*;
import com.tny.game.net.message.protoex.ProtoExCodec;
import io.netty.channel.*;

public abstract class ChannelMaker<C extends Channel> {

    protected DataPacketEncoder encoder;

    protected DataPacketDecoder decoder;


    public ChannelMaker() {
        super();
        ProtoExCodec coder = new ProtoExCodec();
        this.encoder = new DataPacketV1Encoder(null);
        this.decoder = new DataPacketV1Decoder(null);
    }

    public ChannelMaker(
            DataPacketEncoder encoder, DataPacketDecoder decoder) {
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
        // channel.attr(NettyAttrKeys.DATA_PACKET_DECODER)
        //         .set(this.decoder);
        // channel.attr(NettyAttrKeys.DATA_PACKET_ENCODER)
        //         .set(this.encoder);
        this.postInitChannel(channel);
    }

    protected void postAddCoder(ChannelPipeline channelPipeline) {
    }

    protected void prepareAddCoder(ChannelPipeline channelPipeline) {
    }

    protected void postInitChannel(C channel) {
    }

    // public void setCheckers(List<ControllerChecker> checkers) {
    //     this.checkers = checkers == null ? ImmutableList.of() : ImmutableList.copyOf(checkers);
    // }

}
