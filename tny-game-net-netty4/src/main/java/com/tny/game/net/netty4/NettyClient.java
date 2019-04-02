package com.tny.game.net.netty4;

import com.google.common.base.MoreObjects;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;

import static com.tny.game.net.endpoint.listener.ClientEventBuses.*;
import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public class NettyClient<UID> extends AbstractEndpoint<UID> implements NettyTerminal<UID>, Client<UID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private static final ExecutorService CONNECT_EXECUTOR_SERVICE = new StandardThreadExecutor(1, 30,
            20000, new CoreThreadFactory("NettyClientConnect"));

    private URL url;

    private NettyTerminalTunnel<UID> tunnel;

    private NettyClientGuide guide;

    private PostConnect<UID> postConnect;

    public NettyClient(NettyClientGuide guide, URL url, UID unloginUid, PostConnect<UID> postConnect,
                       EndpointEventHandler<UID, NetEndpoint<UID>> endpointEventHandler, int cacheSentMessageSize) {
        super(unloginUid, endpointEventHandler, cacheSentMessageSize);
        this.url = url;
        this.guide = guide;
        this.postConnect = postConnect;
        this.certificate = Certificates.createUnautherized(unloginUid);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public long getConnectTimeout() {
        return url.getParameter(CONNECT_TIMEOUT_URL_PARAM, CONNECT_TIMEOUT_DEFAULT_VALUE);
    }

    @Override
    public boolean getInitConnectAsyn() {
        return url.getParameter(INIT_CONNECT_ASYN_URL_PARAM, INIT_CONNECT_ASYN_DEFAULT_VALUE);
    }

    @Override
    public Certificate<UID> getCertificate() {
        return certificate;
    }

    void open() {
        if (this.isClosed())
            return;
        if (this.tunnel != null)
            return;
        NettyTerminalTunnel<UID> newTunnel;
        synchronized (this) {
            if (this.isClosed())
                return;
            if (this.tunnel != null)
                return;
            newTunnel = this.tunnel = new NettyTerminalTunnel<>(this, guide.getMessageFactory());
        }
        openTunnel(newTunnel, this.getInitConnectAsyn());
        buses().openEvent().notify(this);
    }

    private void openTunnel(NettyTerminalTunnel<UID> tunnel, boolean asyn) {
        if (this.isClosed())
            return;
        if (asyn) {
            CONNECT_EXECUTOR_SERVICE.submit(() -> connectTunnel(tunnel));
        } else {
            connectTunnel(tunnel);
        }
    }

    private void connectTunnel(NettyTerminalTunnel<UID> tunnel) {
        if (tunnel.isAvailable())
            return;
        Lock lock = tunnel.getLock();
        lock.lock();
        try {
            if (tunnel.isAvailable())
                return;
            LOGGER.info("try connect {}", this);
            if (tunnel.open()) {
                buses().activateEvent().notify(this, tunnel);
            }
        } catch (Exception e) {
            LOGGER.error("{} reconnect {} exception", this, tunnel, e);
        } finally {
            lock.unlock();
        }
    }

    private void reconnectTunnel(NettyTerminalTunnel<UID> tunnel) {
        if (this.isClosed())
            return;
        Lock lock = tunnel.getLock();
        if (lock.tryLock()) {
            try {
                int times = tunnel.getFailedTimes();
                if (times >= 3) {
                    LOGGER.warn("{} reconnect {} failed times {} >= 3, stop reconnect", this, tunnel, times);
                    tunnel.resetFailedTimes();
                    return;
                }
                LOGGER.info("{} is ready to submit reconnect {} task for the {} time ", this, tunnel, times);
                if (!tunnel.isAvailable() && tunnel.getState() != TunnelState.INIT) {
                    LOGGER.info("{} submit reconnect {} task for the {} time", this, tunnel, times);
                    tunnel.reset();
                    CONNECT_EXECUTOR_SERVICE.submit(() -> connectTunnel(tunnel));
                }
            } finally {
                lock.unlock();
            }
        } else {
            LOGGER.info("{} reconnect try lock failed", this);
        }
    }

    @Override
    protected NetTunnel<UID> currentTunnel() {
        return this.tunnel;
    }

    @Override
    public Channel connect() throws NetException {
        return guide.connect(url, getConnectTimeout());
    }

    @Override
    public void connectSuccess(NettyTerminalTunnel<UID> tunnel) {
        if (this.tunnel == tunnel) {
            try {
                if (!postConnect.onConnected(tunnel))
                    throw new TunnelException("{} tunnel post connect failed", tunnel);
            } catch (Exception e) {
                throw new TunnelException(e, "{} tunnel post connect failed", tunnel);
            }
        }
    }


    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        buses().unactivatedEvent().notify(this, tunnel);
        if (isOffline())
            return;
        synchronized (this) {
            if (isOffline())
                return;
            Tunnel<UID> currentTunnel = this.currentTunnel();
            if (!currentTunnel.isClosed())
                return;
            setOffline();
            reconnectTunnel((NettyTerminalTunnel<UID>) tunnel);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", url)
                .toString();
    }

}
