// package com.tny.game.net.netty;
//
// import com.tny.game.common.concurrent.StageableFuture;
// import com.tny.game.common.config.Config;
// import com.tny.game.common.result.ResultCodes;
// import com.tny.game.common.utils.URL;
// import com.tny.game.net.base.*;
// import com.tny.game.net.exception.*;
// import com.tny.game.net.transport.*;
// import com.tny.game.net.transport.message.*;
// import io.netty.channel.*;
// import org.slf4j.*;
//
// import java.util.concurrent.*;
// import java.util.function.BiFunction;
//
// import static com.tny.game.net.utils.NetConfigs.*;
// import static org.apache.commons.lang3.ObjectUtils.*;
//
// /**
//  * Created by Kun Yang on 2017/9/11.
//  */
// public class NettyClientTunnel<UID> extends NettyTunnel<UID> {
//
//     public static final Logger LOGGER = LoggerFactory.getLogger(NettyClientTunnel.class);
//
//     private URL url;
//
//     protected BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> loginContentCreator;
//
//     private long loginTimeout;
//     private long sendTimeout;
//     private int resendTimes;
//     private int reconnectTimes;
//
//     public NettyClientTunnel(URL url, Channel channel, AppConfiguration configuration) {
//         this(url, channel, configuration.getProperties(), configuration.getSessionFactory(), configuration.getMessageBuilderFactory(), null);
//     }
//
//
//     public NettyClientTunnel(URL url, Channel channel, AppConfiguration configuration, BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> loginContentCreator) {
//         this(url, channel, configuration.getProperties(), configuration.getSessionFactory(), configuration.getMessageBuilderFactory(), loginContentCreator);
//     }
//
//     public NettyClientTunnel(URL url, Channel channel, Config config, SessionFactory<UID> sessionFactory, MessageBuilderFactory<UID> messageBuilderFactory, BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> loginContentCreator) {
//         super(channel);
//         this.url = url;
//         this.loginContentCreator = loginContentCreator;
//         loginTimeout = url.getParameter(LOGIN_TIMEOUT_URL_PARAM, config.getLong(LOGIN_TIMEOUT_URL_PARAM, 15000L));
//         sendTimeout = url.getParameter(SEND_TIMEOUT_URL_PARAM, config.getLong(SEND_TIMEOUT_URL_PARAM, 0L));
//         resendTimes = url.getParameter(RESEND_TIMES_URL_PARAM, config.getInt(RESEND_TIMES_URL_PARAM, 1));
//         reconnectTimes = url.getParameter(CONNECT_TIMEOUT_URL_PARAM, config.getInt(CONNECT_TIMEOUT_URL_PARAM, 3));
//         this.init(sessionFactory, messageBuilderFactory);
//     }
//
//     public BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> getLoginContentCreator() {
//         return loginContentCreator;
//     }
//
//     public URL getUrl() {
//         return url;
//     }
//
//
//     public long getLoginTimeout() {
//         return loginTimeout;
//     }
//
//     public long getSendTimeout() {
//         return sendTimeout;
//     }
//
//     public int getResendTimes() {
//         return resendTimes;
//     }
//
//
//     boolean resetChannel(Channel channel) {
//         if (!this.channel.isActive() && channel.isActive()) {
//             this.channel = channel;
//             return true;
//         }
//         return false;
//     }
//
//     private static class NextTaskAction extends RecursiveAction {
//
//         @Override
//         protected void compute() {
//             ForkJoinTask<?> task = ForkJoinTask.pollTask();
//             if (task != null) {
//                 task.quietlyInvoke();
//             } else {
//                 try {
//                     Thread.sleep(20);
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 }
//             }
//         }
//
//     }
//
//     private static NextTaskAction action = new NextTaskAction();
//
//     @SuppressWarnings("unchecked")
//     private void reconnect() throws TunnelWriteException {
//         if (this.isClosed()) {
//             int time = max(this.reconnectTimes, 0);
//             for (int i = 1; i <= time; i++) {
//                 try {
//                     synchronized (this) {
//                         if (this.isClosed())
//                             return;
//                         NettyConnector<UID> client = this.channel.attr(NettyAttrKeys.CLIENT).get();
//                         client.reconnect(this);
//                     }
//                     this.login(this.getLoginTimeout());
//                 } catch (TunnelWriteException e) {
//                     LOGGER.warn("{} 发送消息失败", this, e);
//                     this.close();
//                     throw e;
//                 } catch (Exception e) {
//                     LOGGER.warn("{} 发送消息失败", this, e);
//                     this.close();
//                     throw new TunnelWriteException(e);
//                 }
//             }
//         }
//     }
//
//
//     @Override
//     public void doWrite(Message<UID> message, WriteCallback<UID> callback) throws TunnelWriteException {
//         doCheckAndReconnect(message, callback);
//         int time = max(this.getResendTimes() + 1, 1);
//         try {
//             for (int i = 0; i < time; i++) {
//                 try {
//                     long sendTimeout = getSendTimeout();
//                     NetLogger.logSend(this.session, message);
//                     ChannelFuture future = channel.writeAndFlush(message);
//                     if (callback != null) {
//                         future.addListener(f -> {
//                             if (f.isSuccess()) {
//                                 callback.onWrite(message, true, null);
//                             } else {
//                                 callback.onWrite(message, false, f.cause());
//                             }
//                         });
//                     }
//                     if (sendTimeout <= 0)
//                         return;
//                     this.doWait(sendTimeout, future);
//                     return;
//                 } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                     if (i == time - 1)
//                         throw e;
//                     this.session.close();
//                     LOGGER.warn("{} 发送消息失败 第 {} 次", this, i, e);
//                 }
//             }
//         } catch (Exception e) {
//             if (callback != null)
//                 callback.onWrite(message, false, e);
//             LOGGER.warn("{} 发送消息失败", this, e);
//             throw new TunnelWriteException(e);
//         }
//     }
//
//     void login() throws TimeoutException, InterruptedException, ExecutionException, TunnelWriteException {
//         login(getLoginTimeout());
//     }
//
//     private void doCheckAndReconnect(Message<UID> message, WriteCallback<UID> callback) {
//         if (!this.isClosed()) {
//             try {
//                 reconnect();
//             } catch (TunnelWriteException e) {
//                 if (callback != null)
//                     callback.onWrite(message, false, e);
//                 this.session.close();
//             }
//         }
//     }
//
//
//
//     @SuppressWarnings("unchecked")
//     private void login(long timeout) throws TimeoutException, InterruptedException, ExecutionException, TunnelWriteException {
//         if (loginContentCreator != null) {
//             NetSession<UID> oldSession = this.session;
//             boolean relogin = oldSession != null && oldSession.isLogin();
//             MessageContent<UID> loginMessage = loginContentCreator.apply(relogin, this);
//             StageableFuture<Message<UID>> loginFuture = loginMessage.messageFuture(timeout + 10000L);
//             MessageSendEvent<UID> loginEvent = new MessageSendEvent<>(this, loginMessage);
//             this.session.write(loginEvent);
//             this.doWait(timeout, loginFuture);
//             Message result = loginFuture.get();
//             if (!ResultCodes.isSuccess(result.getCode()))
//                 throw new TunnelWriteException(new ValidatorFailException(NetResponseCode.VALIDATOR_FAIL));
//         }
//     }
//
//     private <T> void doWait(long timeout, Future<T> future) throws TimeoutException, ExecutionException, InterruptedException {
//         if (timeout > 0) {
//             if (ForkJoinTask.inForkJoinPool()) {
//                 long loginTimeoutTime = System.currentTimeMillis() + timeout;
//                 while (!future.isDone()) {
//                     if (System.currentTimeMillis() > loginTimeoutTime)
//                         throw new TimeoutException();
//                     action.compute();
//                 }
//             }
//             future.get(timeout, TimeUnit.MILLISECONDS);
//         }
//     }
//
// }
