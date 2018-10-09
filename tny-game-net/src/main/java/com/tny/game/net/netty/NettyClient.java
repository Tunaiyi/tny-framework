package com.tny.game.net.netty;

import com.tny.game.common.utils.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.MessageSubject;
import io.netty.channel.Channel;
import org.slf4j.*;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public class NettyClient<UID> implements Communicator<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private URL url;

    private NettyConnector<UID> connector;

    private NetSession<UID> seesion;

    private Predicate<Tunnel<UID>> postConnect;

    @Override
    public UID getUserId() {
        return null;
    }

    @Override
    public String getUserType() {
        return this.seesion.getUserType();
    }

    @Override
    public Certificate<UID> getCertificate() {
        return null;
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public void close() {
        seesion.close();
    }

    @Override
    public SendContext<UID> sendAsyn(MessageSubject subject, MessageContext<UID> messageContext) {
        return null;
    }

    @Override
    public SendContext<UID> sendSync(MessageSubject subject, MessageContext<UID> messageContext, long timeout) throws NetException {
        return null;
    }

    @Override
    public boolean isClosed() {
        return seesion.isClosed();
    }

    @SuppressWarnings("unchecked")
    boolean connectTunnel(NettyClientTunnel<UID> tunnel) throws NetException, ValidatorFailException {
        Throws.checkNotNull(tunnel, "tunnel is null");
        if (tunnel.isActive())
            return false;
        Optional<Channel> channelOptional = connector.connect(tunnel.getUrl(), tunnel.getConnectTimeout());
        if (channelOptional.isPresent()) {
            Channel channel = channelOptional.get();
            channel.attr(NettyAttrKeys.TUNNEL).set(tunnel);
            tunnel.updateChannel(channel);
            postConnect.test(tunnel);
            if (tunnel.isLogin()) {

            }
            return true;
        }
        seesion.acceptTunnel(tunnel);
        return false;
    }

}
