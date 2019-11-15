package com.tny.game.net.netty4;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyTerminalTunnel<UID> extends NettyTunnel<UID, NettyTerminal<UID>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyTerminalTunnel.class);

    private int failedTimes = 0;

    public NettyTerminalTunnel(NettyTerminal<UID> client, MessageFactory<UID> messageBuilderFactory) {
        super(null, TunnelMode.CLIENT, client, messageBuilderFactory);
    }

    void reset() {
        if (state == TunnelState.INIT)
            return;
        this.lock.lock();
        try {
            if (state == TunnelState.INIT)
                return;
            if (!this.isAvailable())
                this.doDisconnect();
            this.state = TunnelState.INIT;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    protected boolean onOpen() {
        if (!this.isAvailable()) {
            try {
                this.state = TunnelState.INIT;
                Channel channel = endpoint.connect();
                if (channel != null) {
                    this.channel = channel;
                    this.state = TunnelState.ACTIVATE;
                    channel.attr(NettyAttrKeys.TUNNEL).set(this);
                    endpoint.connectSuccess(this);
                    this.resetFailedTimes();
                    return true;
                }
                this.failedTimes++;
            } catch (Exception e) {
                this.failedTimes++;
                this.disconnect();
                throw new TunnelException(e, "{} failed to connect to server", this);
            }
        }
        LOGGER.warn("{} is available", this);
        return false;
    }

    @Override
    protected void onWriteUnavailable(Message<UID> message, WriteMessagePromise promise) {
        this.endpoint.reconnectTunnel(this);
    }

    int getFailedTimes() {
        return failedTimes;
    }

    void resetFailedTimes() {
        this.failedTimes = 0;
    }

    @Override
    public String toString() {
        return "NettyClientTunnel{" + "channel=" + channel + '}';
    }

    @Override
    public boolean bind(NetEndpoint<UID> endpoint) {
        return this.endpoint == endpoint;
    }

    // @SuppressWarnings("unchecked")
    // private void reconnect() throws TunnelWriteException {
    //     if (this.isClosed()) {
    //         int time = max(this.reconnectTimes, 0);
    //         for (int i = 1; i <= time; i++) {
    //             try {
    //                 synchronized (this) {
    //                     if (this.isClosed())
    //                         return;
    //                     NettyConnector<UID> client = this.channel.attr(NettyAttrKeys.CLIENT).get();
    //                     client.reconnect(this);
    //                 }
    //                 this.login(this.getLoginTimeout());
    //             } catch (TunnelWriteException e) {
    //                 LOGGER.warn("{} 发送消息失败", this, e);
    //                 this.close();
    //                 throw e;
    //             } catch (Exception e) {
    //                 LOGGER.warn("{} 发送消息失败", this, e);
    //                 this.close();
    //                 throw new TunnelWriteException(e);
    //             }
    //         }
    //     }
    // }
    //
    // @Override
    // public void doWrite(Message<UID> message, WriteCallback<UID> callback) throws TunnelWriteException {
    //     doCheckAndReconnect(message, callback);
    //     int time = max(this.getResendTimes() + 1, 1);
    //     try {
    //         for (int i = 0; i < time; i++) {
    //             try {
    //                 long sendTimeout = getSendTimeout();
    //                 NetLogger.logSend(this.session, message);
    //                 ChannelFuture future = channel.writeAndFlush(message);
    //                 if (callback != null) {
    //                     future.addListener(f -> {
    //                         if (f.isSuccess()) {
    //                             callback.onWrite(message, true, null);
    //                         } else {
    //                             callback.onWrite(message, false, f.cause());
    //                         }
    //                     });
    //                 }
    //                 if (sendTimeout <= 0)
    //                     return;
    //                 this.doWait(sendTimeout, future);
    //                 return;
    //             } catch (InterruptedException | ExecutionException | TimeoutException e) {
    //                 if (i == time - 1)
    //                     throw e;
    //                 this.session.close();
    //                 LOGGER.warn("{} 发送消息失败 第 {} 次", this, i, e);
    //             }
    //         }
    //     } catch (Exception e) {
    //         if (callback != null)
    //             callback.onWrite(message, false, e);
    //         LOGGER.warn("{} 发送消息失败", this, e);
    //         throw new TunnelWriteException(e);
    //     }
    // }
    //
    // void login() throws TimeoutException, InterruptedException, ExecutionException, TunnelWriteException {
    //     login(getLoginTimeout());
    // }
    //
    // private void doCheckAndReconnect(Message<UID> message, WriteCallback<UID> callback) {
    //     if (!this.isClosed()) {
    //         try {
    //             reconnect();
    //         } catch (TunnelWriteException e) {
    //             if (callback != null)
    //                 callback.onWrite(message, false, e);
    //             this.session.close();
    //         }
    //     }
    // }
    //
    //
    // @SuppressWarnings("unchecked")
    // private void login(long timeout) throws TimeoutException, InterruptedException, ExecutionException, TunnelWriteException {
    //     if (postConnect != null) {
    //         NetSession<UID> oldSession = this.session;
    //         boolean relogin = oldSession != null && oldSession.isLogin();
    //         MessageContent<UID> loginMessage = postConnect.apply(relogin, this);
    //         StageableFuture<Message<UID>> loginFuture = loginMessage.messageFuture(timeout + 10000L);
    //         MessageSendEvent<UID> loginEvent = new MessageSendEvent<>(this, loginMessage);
    //         this.session.write(loginEvent);
    //         this.doWait(timeout, loginFuture);
    //         Message result = loginFuture.get();
    //         if (!ResultCodes.isSuccess(result.getCode()))
    //             throw new TunnelWriteException(new ValidatorFailException(NetResponseCode.VALIDATOR_FAIL));
    //     }
    // }
    //
    // private <T> void doWait(long timeout, Future<T> future) throws TimeoutException, ExecutionException, InterruptedException {
    //     if (timeout > 0) {
    //         if (ForkJoinTask.inForkJoinPool()) {
    //             long loginTimeoutTime = System.currentTimeMillis() + timeout;
    //             while (!future.isDone()) {
    //                 if (System.currentTimeMillis() > loginTimeoutTime)
    //                     throw new TimeoutException();
    //                 action.compute();
    //             }
    //         }
    //         future.get(timeout, TimeUnit.MILLISECONDS);
    //     }
    // }
    //
    // @Override
    // public String toString() {
    //     return MoreObjects.toStringHelper(this)
    //             .add("channel", channel)
    //             .add("url", url)
    //             .add("state", state)
    //             .toString();
    //
    // }
}
