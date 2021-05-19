package com.tny.game.net.netty4;

import com.google.common.base.MoreObjects;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.url.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

import java.util.concurrent.*;

import static com.tny.game.net.endpoint.listener.ClientEventBuses.*;
import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public class NettyClient<UID> extends AbstractEndpoint<UID> implements NettyTerminal<UID>, Client<UID> {

    private static final ScheduledExecutorService CONNECT_EXECUTOR_SERVICE = Executors
            .newScheduledThreadPool(1, new CoreThreadFactory("NettyClientConnect"));

    private final URL url;

    private final NettyClientGuide guide;

    private final PostConnect<UID> postConnect;

    private volatile NettyTunnelConnector connector;

    public NettyClient(NettyClientGuide guide, URL url, PostConnect<UID> postConnect, EndpointContext<UID> endpointContext) {
        super(null, endpointContext);
        this.url = url;
        this.guide = guide;
        this.postConnect = postConnect;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public long getConnectTimeout() {
        return this.url.getParameter(CONNECT_TIMEOUT_URL_PARAM, CONNECT_TIMEOUT_DEFAULT_VALUE);
    }

    @Override
    public boolean isAsyncConnect() {
        return this.url.getParameter(CONNECT_ASYNC_URL_PARAM, CONNECT_ASYNC_DEFAULT_VALUE);
    }

    @Override
    public int getConnectRetryTimes() {
        return this.url.getParameter(RETRY_TIMES_URL_PARAM, RETRY_TIMES_DEFAULT_VALUE);
    }

    @Override
    public long getConnectRetryInterval() {
        return this.url.getParameter(RETRY_INTERVAL_URL_PARAM, RETRY_INTERVAL_DEFAULT_VALUE);
    }

    @Override
    public Certificate<UID> getCertificate() {
        return this.certificate;
    }

    void open() {
        if (this.isClosed()) {
            return;
        }
        if (this.tunnel != null) {
            return;
        }
        NetTunnel<UID> newTunnel;
        int retryTimes = this.getConnectRetryTimes();
        synchronized (this) {
            if (this.isClosed()) {
                return;
            }
            if (this.tunnel != null) {
                return;
            }
            this.tunnel = newTunnel = new GeneralClientTunnel<UID, NetTerminal<UID>>(this.guide.getContext()) {
            };
            newTunnel.bind(this);
            this.connector = new NettyTunnelConnector(newTunnel, retryTimes, this.getConnectRetryInterval());
        }
        this.connect(this.isAsyncConnect(), retryTimes > 1);
        buses().openEvent().notify(this);
    }

    private void connect(boolean async, boolean retry) {
        ScheduledExecutorService executor = async ? CONNECT_EXECUTOR_SERVICE : null;
        if (retry) {
            this.connector.connect(executor);
        } else {
            this.connector.reconnect(executor);
        }
    }

    @Override
    public void reconnect() {
        this.connect(true, true);
    }

    @Override
    protected NetTunnel<UID> currentTunnel() {
        return this.tunnel;
    }

    @Override
    public NetTransport<UID> connect() throws NetException {
        Channel channel = this.guide.connect(this.url, getConnectTimeout());
        return new NettyChannelTransport<>(channel);
    }

    @Override
    public void onConnected(NetTunnel<UID> tunnel) {
        if (this.tunnel == tunnel) {
            if (this.isClosed()) {
                tunnel.close();
                throw new TunnelCloseException("{} tunnel is closed", tunnel);
            }
            try {
                if (!this.postConnect.onConnected(tunnel)) {
                    throw new TunnelException("{} tunnel post connect failed", tunnel);
                }
                buses().activateEvent().notify(this, this.tunnel);
            } catch (Exception e) {
                throw new TunnelException(e, "{} tunnel post connect failed", tunnel);
            }
        }
    }

    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        buses().unactivatedEvent().notify(this, tunnel);
        if (isOffline()) {
            this.reconnect();
            return;
        }
        synchronized (this) {
            if (isOffline()) {
                this.reconnect();
                return;
            }
            Tunnel<UID> currentTunnel = this.currentTunnel();
            if (currentTunnel.isClosed()) {
                return;
            }
            setOffline();
            this.reconnect();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", this.url)
                .toString();
    }

}
