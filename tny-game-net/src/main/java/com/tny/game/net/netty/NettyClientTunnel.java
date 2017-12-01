package com.tny.game.net.netty;

import com.tny.game.common.config.Config;
import com.tny.game.common.result.ResultCodes;
import com.tny.game.common.utils.URL;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageWriteFuture;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.event.SessionSendEvent;
import com.tny.game.net.tunnel.Tunnel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import static com.tny.game.net.base.AppConstants.*;
import static org.apache.commons.lang3.ObjectUtils.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyClientTunnel<UID> extends NettyTunnel<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyClientTunnel.class);

    private URL url;

    private BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> loginContentCreator;

    private long loginTimeout;
    private long sendTimeout;
    private int resendTimes;
    private int reconnectTimes;


    public NettyClientTunnel(URL url, Channel channel, AppConfiguration configuration, BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> loginContentCreator) {
        super(channel, configuration);
        this.url = url;
        this.loginContentCreator = loginContentCreator;
        Config config = configuration.getProperties();
        loginTimeout = url.getParameter(LOGING_TIMEOUT_URL_PARAM, config.getLong(LOGING_TIMEOUT_URL_PARAM, 15000L));
        sendTimeout = url.getParameter(SEND_TIMEOUT_URL_PARAM, config.getLong(SEND_TIMEOUT_URL_PARAM, 0L));
        resendTimes = url.getParameter(RESEND_TIMES_URL_PARAM, config.getInt(RESEND_TIMES_URL_PARAM, 1));
        reconnectTimes = url.getParameter(CONNECT_TIMEOUT_URL_PARAM, config.getInt(CONNECT_TIMEOUT_URL_PARAM, 0));
    }

    public BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> getLoginContentCreator() {
        return loginContentCreator;
    }

    public URL getUrl() {
        return url;
    }


    public long getLoginTimeout() {
        return loginTimeout;
    }

    public long getSendTimeout() {
        return sendTimeout;
    }

    public int getResendTimes() {
        return resendTimes;
    }


    void resetChannel(Channel channel) {
        if (!this.channel.isActive() && channel.isActive())
            this.channel = channel;
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

    @SuppressWarnings("unchecked")
    private void reconnect() throws InterruptedException, ExecutionException, TimeoutException, DispatchException {
        if (!this.isConnected()) {
            int time = max(this.reconnectTimes, 0);
            for (int i = 1; i <= time; i++) {
                try {
                    synchronized (this) {
                        if (this.isConnected())
                            return;
                        NettyClient<UID> client = this.channel.attr(NettyAttrKeys.CLIENT).get();
                        client.reconnect(this);
                    }
                    this.login(this.getLoginTimeout());
                } catch (Throwable e) {
                    this.close();
                    LOGGER.warn("{} 发送消息失败", this, e);
                    if (i >= time) {
                        this.session.close();
                        throw e;
                    }
                }
            }
        }
    }

    @Override
    public void write(Message<UID> message, MessageWriteFuture<UID> writeFuture) throws TimeoutException, ExecutionException, InterruptedException, DispatchException {
        int time = max(this.getResendTimes() + 1, 1);
        for (int i = 0; i < time; i++) {
            if (!this.isConnected()) {
                try {
                    reconnect();
                } catch (Throwable e) {
                    if (writeFuture != null)
                        writeFuture.fail(e);
                    this.session.close();
                    throw e;
                }
            }
            try {
                long sendTimeout = getSendTimeout();
                NetLogger.logSend(this.session, message);
                ChannelFuture future = channel.writeAndFlush(message);
                if (writeFuture != null && writeFuture.isHasFuture()) {
                    future.addListener(f -> {
                        if (f.isSuccess()) {
                            writeFuture.success(message);
                        } else {
                            writeFuture.fail(f.cause());
                        }
                    });
                }
                if (sendTimeout <= 0)
                    return;
                this.doWait(sendTimeout, future);
                return;
            } catch (Throwable e) {
                if (i >= time)
                    throw e;
                this.session.close();
                LOGGER.warn("{} 发送消息失败", this, e);
            }
        }

    }

    void login() throws TimeoutException, InterruptedException, ExecutionException, DispatchException {
        login(getLoginTimeout());
    }

    @SuppressWarnings("unchecked")
    private void login(long timeout) throws TimeoutException, ExecutionException, InterruptedException, DispatchException {
        if (loginContentCreator != null) {
            Session<UID> oldSession = this.session;
            boolean relogin = oldSession != null && oldSession.getCertificate().isLogin();
            MessageContent<UID> loginMessage = loginContentCreator.apply(relogin, this);
            CompletableFuture<Message<UID>> loginFuture = loginMessage.messageFuture(timeout + 10000L);
            SessionSendEvent<UID> loginEvent = new SessionSendEvent<>(this, loginMessage);
            Message<UID> message = this.session.createMessage(this, loginMessage);
            this.write(message, loginEvent);
            this.doWait(timeout, loginFuture);
            Message result = loginFuture.get();
            if (!ResultCodes.isSuccess(result.getCode()))
                throw new DispatchException(ResultCodes.of(result.getCode()));
        }
    }

    private <T> void doWait(long timeout, Future<T> future) throws TimeoutException, ExecutionException, InterruptedException {
        if (timeout > 0) {
            if (ForkJoinTask.inForkJoinPool()) {
                long loginTimeoutTime = System.currentTimeMillis() + timeout;
                while (!future.isDone()) {
                    if (System.currentTimeMillis() > loginTimeoutTime)
                        throw new TimeoutException();
                    action.compute();
                }
            }
            future.get(timeout, TimeUnit.MILLISECONDS);
        }
    }

}
