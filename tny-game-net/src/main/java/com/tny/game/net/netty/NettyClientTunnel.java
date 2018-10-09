package com.tny.game.net.netty;

import com.tny.game.common.config.Config;
import com.tny.game.common.utils.URL;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;
import org.slf4j.*;

import java.util.concurrent.*;

import static com.tny.game.net.utils.NetConfigs.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyClientTunnel<UID> extends NettyTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyClientTunnel.class);

    private URL url;

    private NettyClient<UID> client;

    private Config config;

    public NettyClientTunnel(URL url, Certificate<UID> certificate, Channel channel, AppConfiguration<UID> configuration) {
        this(url, certificate, channel, configuration.getProperties());
        this.setMessageFactory(configuration.getMessageFactory())
                .setMessageHandler(configuration.getMessageHandler());
    }

    public NettyClientTunnel(URL url, Certificate<UID> certificate, Channel channel, Config config) {
        super(channel, certificate, TunnelMode.CLIENT);
        this.url = url;
        this.config = config;
    }


    public URL getUrl() {
        return url;
    }

    long getConnectTimeout() {
        return url.getParameter(CONNECT_TIMEOUT_URL_PARAM, config.getLong(CONNECT_TIMEOUT_URL_PARAM, 5000L));
    }

    private void reconnet() {

    }

    public void borrow() {

    }

    @Override
    public synchronized void open() throws NetException, ValidatorFailException {
        if (this.isClosed())
            client.connectTunnel(this);
    }

    boolean updateChannel(Channel channel) {
        if (!this.channel.isActive() && channel.isActive()) {
            this.channel = channel;
            return true;
        }
        return false;
    }

    private static class NextTaskAction extends RecursiveAction {

        @Override
        protected void compute() {
            ForkJoinTask<?> task = ForkJoinTask.pollTask();
            if (task != null) {
                task.quietlyInvoke();
            } else {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static NextTaskAction action = new NextTaskAction();

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

}
