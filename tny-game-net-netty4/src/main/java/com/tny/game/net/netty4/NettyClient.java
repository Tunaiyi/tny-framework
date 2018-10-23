package com.tny.game.net.netty4;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.utils.URL;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.MessageSubject;
import com.tny.game.net.session.AbstractSession;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public class NettyClient<UID> extends AbstractSession<UID> implements Client<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private AtomicInteger indexCounter = new AtomicInteger(0);

    private URL url;

    private List<NettyClientTunnel<UID>> tunnels = ImmutableList.of();

    private NettyClientBootstrap<UID> connector;

    private Predicate<Tunnel<UID>> postConnect;

    public NettyClient(Certificate<UID> certificate) {
        super(certificate);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void offline() {
        synchronized (this) {
            for (NetTunnel<UID> tunnel : this.tunnels) {
                if (!tunnel.isClosed())
                    tunnel.close();
            }
            this.tunnels.clear();
            this.setOffline();
        }
    }

    @Override
    public void heartbeat() {
        for (NetTunnel<UID> tunnel : tunnels)
            tunnel.ping();
    }

    @Override
    protected NetTunnel<UID> selectTunnel(MessageSubject subject, MessageContext<UID> messageContext) {
        int startIndex = indexCounter.incrementAndGet();
        int size = tunnels.size();
        if (size == 0)
            throw new SessionException(format("{} no tunnels exist", this));
        for (int index = startIndex; index < startIndex + size; index++) {
            NetTunnel<UID> tunnel = tunnels.get(index % size);
            if (tunnel.isAvailable())
                return tunnel;
        }
        throw new SessionException(format("select tunnel {} is close", this));
    }

    @SuppressWarnings("unchecked")
    Channel connect() throws NetException {
        return connector.connect(url, getConnectTimeout());
    }

    void connectSuccess(NettyClientTunnel<UID> tunnel) {
        if (this.tunnels.contains(tunnel)) {
            if (!postConnect.test(tunnel))
                throw new TunnelException(format("{} tunnel post connect failed", tunnel));
            if (!tunnel.isBind())
                tunnel.bind(this);
        }
    }

    @Override
    protected NetTunnel<UID> doAcceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException {
        throw new UnsupportedOperationException();
    }

    private long getConnectTimeout() {
        return url.getParameter(CONNECT_TIMEOUT_URL_PARAM, CONNECT_TIMEOUT_DEFAULT_VALUE);
    }


    @Override
    public void onDisable(NetTunnel<UID> tunnel) {

    }

}
