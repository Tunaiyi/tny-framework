package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyClientTunnel<UID> extends NettyTunnel<UID, NettyTerminal<UID>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientTunnel.class);

    public NettyClientTunnel(NetBootstrapContext<UID> bootstrapContext) {
        super(null, TunnelMode.CLIENT, bootstrapContext);
    }

    void reset() {
        if (this.status == TunnelStatus.INIT) {
            return;
        }
        synchronized (this) {
            if (this.status == TunnelStatus.INIT) {
                return;
            }
            if (!this.isAvailable()) {
                this.disconnect();
            }
            this.status = TunnelStatus.INIT;
        }
    }

    @Override
    protected boolean onOpen() {
        if (!this.isAvailable()) {
            try {
                this.status = TunnelStatus.INIT;
                Channel channel = this.endpoint.connect();
                if (channel != null) {
                    this.channel = channel;
                    this.status = TunnelStatus.ACTIVATED;
                    channel.attr(NettyAttrKeys.TUNNEL).set(this);
                    this.endpoint.connectSuccess(this);
                    return true;
                }
            } catch (Exception e) {
                this.disconnect();
                throw new TunnelException(e, "{} failed to connect to server", this);
            }
        }
        LOGGER.warn("{} is available", this);
        return false;
    }

    @Override
    protected void onDisconnect() {
        this.disconnectChannel();
    }

    @Override
    protected void onWriteUnavailable(MessageContent content, WriteMessagePromise promise) {
        this.endpoint.reconnect();
    }

    @Override
    public String toString() {
        return "NettyClientTunnel{" + "channel=" + this.channel + '}';
    }

    @Override
    protected boolean replayEndpoint(NetEndpoint<UID> endpoint) {
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
