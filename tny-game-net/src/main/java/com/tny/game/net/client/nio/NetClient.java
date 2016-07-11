package com.tny.game.net.client.nio;

import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.ChannelClientSession;
import com.tny.game.net.dispatcher.NetAttributeKey;
import com.tny.game.net.dispatcher.Session;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NetClient extends ChannelClientSession {

    private static final int CLOSE = 1;
    private static final int UNCONNECTED = 2;
    private static final int CONNECTING = 3;
    private static final int CONNECTED = 4;

    private static final Logger LOG = LoggerFactory.getLogger(CoreLogger.NIO_CLIENT);

    protected AtomicInteger idCreater = new AtomicInteger(0);

    protected long userID = Session.UN_LOGIN_UID;

    protected SocketAddress remoteAddress;

    protected Bootstrap bootstrap;

    protected ConnectedCallback connectedCallback;

    protected AtomicInteger state = new AtomicInteger(NetClient.UNCONNECTED);

    protected String hostName;

    public NetClient(Bootstrap bootstrap, InetSocketAddress remoteAddress) {
        this(bootstrap, remoteAddress, null);
    }

    public NetClient(Bootstrap bootstrap, String host, int port, ConnectedCallback callback) {
        this(bootstrap, new InetSocketAddress(host, port), callback);
    }

    public NetClient(Bootstrap bootstrap, InetSocketAddress remoteAddress, ConnectedCallback callback) {
        super(null);
        this.remoteAddress = remoteAddress;
        this.bootstrap = bootstrap;
        this.attributes().setAttribute(NetAttributeKey.CLEAR_KEY, new AtomicBoolean());
        this.hostName = remoteAddress.getHostName();
        this.connectedCallback = callback;
    }

    public NetClient(Bootstrap bootstrap, String host, int port) {
        this(bootstrap, new InetSocketAddress(host, port));
    }

    public void connect() throws IOException {
        ChannelFuture future = this.doConnect();
        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                try {
                    ConnectedCallback callback = NetClient.this.connectedCallback;
                    if (callback == null)
                        return;
                    try {
                        LoginCertificate certificate = callback.connected(future.isSuccess(), NetClient.this, future.cause());
                        NetClient.this.login(certificate);
                    } catch (Exception exception) {
                        NetClient.LOG.error("", exception);
                    }
                } catch (Exception e2) {
                }
            }

        });
    }

    public void awaitConnect() throws IOException, InterruptedException {
        ChannelFuture future = this.doConnect();
        future.awaitUninterruptibly();
    }

    public void awaitConnect(long timeoutMillis) throws IOException, InterruptedException {
        ChannelFuture future = this.doConnect();
        future.awaitUninterruptibly(timeoutMillis);
    }

    private ChannelFutureListener reconnetter = new ChannelFutureListener() {

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            int stateValue = NetClient.this.state.get();
            if (stateValue != NetClient.CLOSE) {
                NetClient.this.state.set(NetClient.UNCONNECTED);
                NetClient.this.connect();
            }
        }

    };

    private ChannelFutureListener conneteder = new ChannelFutureListener() {

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            try {
                Channel channel = future.channel();
                channel.closeFuture().addListener(NetClient.this.reconnetter);
                if (future.isSuccess()) {
                    int stateValue = NetClient.this.state.get();
                    if (stateValue == NetClient.CONNECTING && NetClient.this.state.compareAndSet(stateValue, NetClient.CONNECTED)) {
                        NetClient.this.setChannel(channel);
                        NetClient.LOG.debug("连接 {} ", NetClient.this.remoteAddress);
                    } else {
                        NetClient.LOG.debug("连接 {} 在连接过程中被断开", NetClient.this.remoteAddress);
                        NetClient.this.disconnect();
                    }
                } else {
                    NetClient.LOG.error("连接 {} 失败", NetClient.this.remoteAddress, future.cause());
                    NetClient.this.connect();
                }
            } catch (Exception e) {
                NetClient.LOG.error("连接 {} 失败", NetClient.this.remoteAddress, e);
            }
        }

    };

    private ChannelFuture doConnect() throws IOException {
        int stateValue = this.state.get();
        if (stateValue == NetClient.CLOSE) {
            IOException exception = new IOException("客户端已经关闭,无法重连! " + this.remoteAddress + "失败");
            throw exception;
        }
        if (stateValue != NetClient.UNCONNECTED || this.isConnect()) {
            IOException exception = new IOException("客户端已连接! " + this.remoteAddress + "失败");
            throw exception;
        }

        if (this.state.compareAndSet(NetClient.UNCONNECTED, NetClient.CONNECTING)) {
            ChannelFuture future = null;
            future = this.bootstrap.connect(this.remoteAddress);
            future.addListener(this.conneteder);
            return future;
        } else {
            if (this.state.get() == NetClient.CLOSE) {
                IOException exception = new IOException("客户端已经关闭,无法重连! " + this.remoteAddress + "失败");
                throw exception;
            } else {
                IOException exception = new IOException("客户端已连接! " + this.remoteAddress + "失败");
                throw exception;
            }
        }
    }

    public boolean isClose() {
        return this.state.get() == NetClient.CLOSE;//this.channel != null && this.channel.isConnected();
    }

    public void close() {
        int stateValue = this.state.get();
        if (stateValue != NetClient.CLOSE) {
            if (this.state.compareAndSet(stateValue, NetClient.CLOSE)) {
                this.disconnect();
            } else {
                this.close();
            }
        }
    }

    public SocketAddress getSocketAddress() {
        return this.remoteAddress;
    }

    @Override
    public String getHostName() {
        return this.hostName;
    }

}
