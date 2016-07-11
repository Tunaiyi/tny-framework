package com.tny.game.net.coder;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.NetAttributeKey;
import com.tny.game.net.dispatcher.message.protoex.ProtoExMessageBuilderFactory;
import com.tny.game.net.dispatcher.message.protoex.ProtoExMessageCoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.util.List;

public abstract class ChannelMaker<C extends Channel> {

    protected List<RequestChecker> checkers;

    protected DataPacketEncoder encoder;

    protected DataPacketDecoder decoder;

    protected MessageBuilderFactory messageBuilderFactory;

    public ChannelMaker(List<RequestChecker> checkers) {
        super();
        this.checkers = checkers == null ? ImmutableList.of() : ImmutableList.copyOf(checkers);
        ProtoExMessageCoder coder = new ProtoExMessageCoder();
        this.encoder = new SimpleDataPacketEncoder(coder, false);
        this.decoder = new SimpleDataPacketDecoder(coder);
        this.messageBuilderFactory = new ProtoExMessageBuilderFactory();
    }

    public ChannelMaker(
            MessageBuilderFactory messageBuilderFactory,
            DataPacketEncoder encoder, DataPacketDecoder decoder,
            List<RequestChecker> checkers) {
        super();
        this.encoder = encoder;
        this.decoder = decoder;
        this.checkers = checkers == null ? ImmutableList.of() : ImmutableList.copyOf(checkers);
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
        channel.attr(NetAttributeKey.REQUEST_CHECKERS)
                .set(this.checkers);
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

    public void setCheckers(List<RequestChecker> checkers) {
        this.checkers = checkers == null ? ImmutableList.of() : ImmutableList.copyOf(checkers);
    }

}
