package com.tny.game.net.netty.coder;

import com.tny.game.net.transport.message.protoex.ProtoExMessageCoder;
import com.tny.game.net.netty.DecoderHandler;
import com.tny.game.net.netty.EncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

public abstract class ChannelMaker<C extends Channel> {

    protected DataPacketEncoder encoder;

    protected DataPacketDecoder decoder;


    public ChannelMaker() {
        super();
        ProtoExMessageCoder coder = new ProtoExMessageCoder();
        this.encoder = new SimpleDataPacketEncoder(coder, false);
        this.decoder = new SimpleDataPacketDecoder(coder);
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
