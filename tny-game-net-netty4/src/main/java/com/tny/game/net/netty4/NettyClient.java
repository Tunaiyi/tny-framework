package com.tny.game.net.netty4;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.URL;
import com.tny.game.net.base.PostConnect;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.endpoint.listener.ClientEventBuses.*;
import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2018/8/28.
 */
public class NettyClient<UID> extends AbstractEndpoint<UID> implements Client<UID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private static final ExecutorService CONNECT_EXECUTOR_SERVICE = new StandardThreadExecutor(1, 30,
            20000, new CoreThreadFactory("NettyClientConnect"));

    private AtomicInteger indexCounter = new AtomicInteger(0);

    private URL url;

    private ClientState state = ClientState.INIT;

    private List<NettyClientTunnel<UID>> tunnels = ImmutableList.of();

    private NettyClientGuide guide;

    private PostConnect<UID> postConnect;

    public NettyClient(NettyClientGuide guide, URL url, Certificate<UID> certificate, PostConnect<UID> postConnect) {
        super(certificate, MessageIdCreator.ENDPOINT_SENDER_MESSAGE_ID_MARK);
        this.url = url;
        this.guide = guide;
        this.postConnect = postConnect;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    private long getConnectTimeout() {
        return url.getParameter(CONNECT_TIMEOUT_URL_PARAM, CONNECT_TIMEOUT_DEFAULT_VALUE);
    }

    private int getConnectionSize() {
        return url.getParameter(CONNECTION_SIZE_URL_PARAM, CONNECTION_SIZE_DEFAULT_VALUE);
    }

    private boolean getInitConnectAsyn() {
        return url.getParameter(INIT_CONNECT_ASYN_URL_PARAM, INIT_CONNECT_ASYN_DEFAULT_VALUE);
    }

    @Override
    public void disconnect() {
        synchronized (this) {
            for (NetTunnel<UID> tunnel : this.tunnels) {
                if (!tunnel.isClosed())
                    tunnel.close();
            }
            this.tunnels.clear();
        }
    }

    @Override
    public void heartbeat() {
        for (NetTunnel<UID> tunnel : tunnels) {
            if (tunnel.isAvailable())
                tunnel.ping();
        }
    }

    synchronized void open() {
        int size = getConnectionSize();
        List<NettyClientTunnel<UID>> tunnels = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            NettyClientTunnel<UID> tunnel = new NettyClientTunnel<>(this, certificate, guide.getMessageFactory());
            tunnels.add(tunnel);
        }
        this.tunnels = ImmutableList.copyOf(tunnels);
        this.initTunnels(tunnels);
        this.state = ClientState.OPEN;
        buses().openEvent().notify(this);
    }

    private void initTunnels(List<NettyClientTunnel<UID>> tunnels) {
        boolean initAsyn = getInitConnectAsyn();
        for (NettyClientTunnel<UID> tunnel : tunnels) {
            openTunnel(tunnel, initAsyn);
        }
    }

    private void openTunnel(NettyClientTunnel<UID> tunnel, boolean asyn) {
        if (this.isClosed())
            return;
        if (asyn) {
            CONNECT_EXECUTOR_SERVICE.submit(() -> connectTunnel(tunnel));
        } else {
            tunnel.open();
        }
    }

    private void connectTunnel(NettyClientTunnel<UID> tunnel) {
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

    private void reconnectTunnel(NettyClientTunnel<UID> tunnel) {
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
                LOGGER.info("{} is ready to submit reconnect task", this, tunnel, times);
                if (!tunnel.isAvailable() && tunnel.getState() != TunnelState.INIT) {
                    LOGGER.info("{} submit reconnect task", this, tunnel, times);
                    tunnel.reset();
                    CONNECT_EXECUTOR_SERVICE.submit(() -> connectTunnel(tunnel));
                }
            } finally {
                lock.unlock();
            }
        } else {
            LOGGER.info("{} reconnect try lock failed", this, tunnel);
        }
    }

    @Override
    protected NetTunnel<UID> selectTunnel(MessageContext<UID> messageContext) {
        int startIndex = indexCounter.incrementAndGet();
        int size = tunnels.size();
        if (size == 0)
            throw new SessionException(format("{} no tunnels exist", this));
        for (int index = startIndex; index < startIndex + size; index++) {
            NettyClientTunnel<UID> tunnel = tunnels.get(index % size);
            if (tunnel.isAvailable())
                return tunnel;
            else
                reconnectTunnel(tunnel);
        }
        throw new SessionException(format("{} not available tunnel", this));
    }

    Channel connect() throws NetException {
        return guide.connect(url, getConnectTimeout());
    }

    void connectSuccess(NettyClientTunnel<UID> tunnel) {
        if (this.tunnels.contains(tunnel)) {
            try {
                if (!postConnect.onConnected(tunnel))
                    throw new TunnelException("{} tunnel post connect failed", tunnel);
            } catch (Exception e) {
                throw new TunnelException(e, "{} tunnel post connect failed", tunnel);
            }
            if (!tunnel.isBind())
                tunnel.bind(this);
        }
    }

    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        buses().unactivatedEvent().notify(this, tunnel);
        reconnectTunnel((NettyClientTunnel<UID>) tunnel);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("url", url)
                .toString();
    }

    @Override
    public boolean isClosed() {
        return this.state == ClientState.CLOSE;
    }

    @Override
    public synchronized void close() {
        if (this.state != ClientState.CLOSE) {
            this.state = ClientState.CLOSE;
            this.disconnect();
            buses().closeEvent().notify(this);
        }
    }

}
