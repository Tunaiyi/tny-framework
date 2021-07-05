package com.tny.game.net.netty4.agency;

import com.tny.game.net.agency.*;
import com.tny.game.net.agency.datagram.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import static com.tny.game.net.netty4.NettyAttrKeys.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:05 下午
 */
public class NettyChannelAgentDatagramTransmitter extends NettyChannelConnection implements AgentDatagramTransmitter {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelTransporter.class);

    public NettyChannelAgentDatagramTransmitter(Channel channel) {
        super(channel);
    }

    @Override
    public WriteMessagePromise createWritePromise(long sendTimeout) {
        return null;
    }

    @Override
    public void close() {
        NetPipe<?> pipe = this.channel.attr(PIPE).getAndSet(null);
        if (pipe != null && pipe.isActive()) {
            pipe.close();
        }
        this.channel.disconnect();
    }

    @Override
    public WriteMessageFuture write(TubuleDatagram datagram, WriteMessagePromise promise) {
        ChannelPromise channelPromise = checkAndCreateChannelPromise(promise);
        this.channel.writeAndFlush(datagram, channelPromise);
        return promise;
    }

    @Override
    public WriteMessageFuture write(AgentDatagramMaker maker, WriteMessagePromise promise) {
        ChannelPromise channelPromise = checkAndCreateChannelPromise(promise);
        this.channel.eventLoop().execute(() -> this.channel.writeAndFlush(maker.make(), channelPromise));
        return promise;
    }

    @Override
    public void bind(GeneralPipe<?> pipe) {
        this.channel.attr(PIPE).set(pipe);
    }

}
