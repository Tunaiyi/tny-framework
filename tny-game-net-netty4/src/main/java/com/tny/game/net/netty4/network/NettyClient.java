package com.tny.game.net.netty4.network;

import com.google.common.base.MoreObjects;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.url.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.*;

import static com.tny.game.net.endpoint.listener.ClientEventBuses.*;
import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public class NettyClient<UID> extends BaseNetEndpoint<UID> implements NettyTerminal<UID>, Client<UID> {

    private static final TunnelConnectExecutor EXECUTOR_SERVICE = new ScheduledTunnelConnectExecutor(
            Executors.newScheduledThreadPool(1, new CoreThreadFactory("NettyClientConnect")));

    private final URL url;

    private final NettyClientGuide guide;

    private final PostConnect<UID> postConnect;

    private volatile TunnelConnector connector;

    private final NetIdGenerator idGenerator;

    public NettyClient(NettyClientGuide guide, NetIdGenerator idGenerator, URL url, PostConnect<UID> postConnect,
            Certificate<UID> certificate, NetworkContext context) {
        super(null, certificate, context);
        this.url = url;
        this.idGenerator = idGenerator;
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
    public boolean isAutoReconnect() {
        ClientConnectorSetting setting = guide.getSetting().getConnector();
        return this.url.getParameter(AUTO_RECONNECT_PARAM, setting.isAutoReconnect());
    }

    @Override
    public int getConnectRetryTimes() {
        ClientConnectorSetting setting = guide.getSetting().getConnector();
        return this.url.getParameter(RETRY_TIMES_URL_PARAM, setting.getRetryTimes());
    }

    @Override
    public List<Long> getConnectRetryIntervals() {
        ClientConnectorSetting setting = guide.getSetting().getConnector();
        String intervals = this.url.getParameter(RETRY_INTERVAL_URL_PARAM, "");
        if (StringUtils.isEmpty(intervals)) {
            return setting.getRetryIntervals();
        }
        String[] data = StringUtils.split(intervals, ",");
        return Stream.of(data)
                .map(Long::parseLong)
                .filter(i -> i > 0)
                .collect(Collectors.toList());
    }

    private ClientConnectFuture<UID> checkPreOpen() {
        if (this.isClosed()) {
            return DefaultClientConnectFuture.closed(this.getUrl());
        }
        return null;
    }

    private void initTunnel() {
        NetTunnel<UID> newTunnel;
        if (this.tunnel != null) {
            return;
        }
        synchronized (this) {
            if (this.tunnel != null) {
                return;
            }
            this.tunnel = newTunnel = new GeneralClientTunnel<>(this.idGenerator.generate(), this.guide.getContext());
            newTunnel.bind(this);
            this.connector = new TunnelConnector(newTunnel, this, EXECUTOR_SERVICE);
        }
    }

    @Override
    public ClientConnectFuture<UID> open() {
        ClientConnectFuture<UID> future = checkPreOpen();
        if (future != null) {
            return future;
        }
        initTunnel();
        DefaultClientConnectFuture<UID> connectFuture = new DefaultClientConnectFuture<>();
        this.doConnect((status, tunnel, cause) -> {
            if (status.isSuccess()) {
                buses().<UID>openEvent().notify(this);
                connectFuture.complete(this);
            } else {
                connectFuture.completeExceptionally(cause);
            }
        });
        return connectFuture;
    }

    private void doConnect(ConnectCallback callback) {
        this.connector.connect(callback);
    }

    @Override
    public void reconnect() {
        this.connector.reconnect();
    }

    @Override
    protected void prepareClose() {
        TunnelConnector connector = this.connector;
        if (connector != null) {
            connector.shutdown();
        }
    }

    @Override
    public MessageTransporter connect() throws NetException {
        Channel channel = this.guide.connect(this.url, getConnectTimeout());
        return new NettyChannelMessageTransporter(channel);
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
                    tunnel.disconnect();
                    throw new TunnelException("{} tunnel post connect failed", tunnel);
                }
                buses().<UID>activateEvent().notify(this, this.tunnel);
            } catch (TunnelException e) {
                throw e;
            } catch (Exception e) {
                throw new TunnelException(e, "{} tunnel post connect failed", tunnel);
            }
        }
    }

    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        buses().<UID>unactivatedEvent().notify(this, tunnel);
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
    protected void postClose() {
        TunnelConnector connector = this.connector;
        connector.shutdown();
        this.connector = null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", this.url)
                .toString();
    }

}
