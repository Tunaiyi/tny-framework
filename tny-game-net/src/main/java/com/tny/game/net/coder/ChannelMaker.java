package com.tny.game.net.coder;

import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.NetAttributeKey;
import com.tny.game.net.dispatcher.message.protoex.ProtoExMessageBuilderFactory;
import com.tny.game.net.dispatcher.message.protoex.ProtoExMessageCoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

public abstract class ChannelMaker<C extends Channel> {

    protected RequestChecker checker;

    protected DataPacketEncoder encoder;

    protected DataPacketDecoder decoder;

    protected MessageBuilderFactory messageBuilderFactory;

    public ChannelMaker(RequestChecker checker) {
        super();
        this.checker = checker;
        ProtoExMessageCoder coder = new ProtoExMessageCoder();
        this.encoder = new SimpleDataPacketEncoder(coder, false);
        this.decoder = new SimpleDataPacketDecoder(coder);
        this.messageBuilderFactory = new ProtoExMessageBuilderFactory();
    }

    public ChannelMaker(
            MessageBuilderFactory messageBuilderFactory,
            DataPacketEncoder encoder, DataPacketDecoder decoder,
            RequestChecker checker) {
        super();
        this.encoder = encoder;
        this.decoder = decoder;
        this.checker = checker;
        this.messageBuilderFactory = messageBuilderFactory;
    }

    public void initChannel(C channel) throws Exception {
        ChannelPipeline channelPipeline = channel.pipeline();
        this.prepareAddCoder(channelPipeline);
        channelPipeline.addLast("frameDecoder", new DecoderHandeler(this.decoder));
        channelPipeline.addLast("encoder", new EncodeHandler(this.encoder));
        this.postAddCoder(channelPipeline);
        channel.attr(NetAttributeKey.MSG_BUILDER_FACTOR)
                .set(this.messageBuilderFactory);
        channel.attr(NetAttributeKey.DATA_PACKET_DECODER)
                .set(this.decoder);
        channel.attr(NetAttributeKey.DATA_PACKET_ENCODER)
                .set(this.encoder);
        channel.attr(NetAttributeKey.REQUEST_CHECKER)
                .set(this.checker);
        this.postInitChannel(channel);
    }

    protected void postAddCoder(ChannelPipeline channelPipeline) {
    }

    protected void prepareAddCoder(ChannelPipeline channelPipeline) {
    }

    protected void postInitChannel(C channel) {
    }

    public MessageBuilderFactory getMessageBuilderFactory() {
        return this.messageBuilderFactory;
    }

    public void setMessageBuilderFactory(MessageBuilderFactory messageBuilderFactory) {
        this.messageBuilderFactory = messageBuilderFactory;
    }

    public void setChecker(RequestChecker checker) {
        this.checker = checker;
    }

}
