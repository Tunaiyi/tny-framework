// package com.tny.game.net.netty;
//
// import com.tny.game.common.concurrent.StageableFuture;
// import com.tny.game.common.result.ResultCodes;
// import com.tny.game.common.utils.URL;
// import com.tny.game.net.base.NetResponseCode;
// import com.tny.game.net.exception.*;
// import com.tny.game.net.message.Message;
// import com.tny.game.net.session.*;
// import com.tny.game.net.tunnel.Tunnel;
// import org.slf4j.*;
//
// import java.util.concurrent.*;
// import java.util.function.BiFunction;
//
// import static org.apache.commons.lang3.ObjectUtils.*;
//
// /**
//  * Created by Kun Yang on 2018/8/28.
//  */
// public class NettyCommunicator<UID> implements Communicator<UID> {
//
//     public static final Logger LOGGER = LoggerFactory.getLogger(NettyCommunicator.class);
//
//     private NettyClient<UID> client;
//
//     private NettyTunnel<UID> tunnel;
//
//     private URL url;
//     private long loginTimeout;
//     private long sendTimeout;
//     private int resendTimes;
//     private int reconnectTimes;
//     private BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> loginContentCreator;
//
//     @Override
//     public UID getUid() {
//         return this.tunnel.getUid();
//     }
//
//     @Override
//     public String getUserGroup() {
//         return this.tunnel.getUserGroup();
//     }
//
//     @Override
//     public void send(MessageContent<UID> content) {
//         this.tunnel.send(content);
//     }
//
//     @Override
//     public void receive(Message<UID> message) {
//         this.tunnel.receive(message);
//     }
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
//                         tunnel = client.doConnect(url);
//                     }
//                     this.login(true, this.loginTimeout);
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
//     @SuppressWarnings("unchecked")
//     private void login(boolean relogin, long timeout) throws InterruptedException, ExecutionException, TunnelWriteException, TimeoutException {
//         if (loginContentCreator != null) {
//             NettyTunnel<UID> tunnel = this.tunnel;
//             MessageContent<UID> loginMessage = loginContentCreator.apply(relogin, tunnel);
//             StageableFuture<Message<UID>> loginFuture = loginMessage.messageFuture(timeout + 10000L);
//             tunnel.send(loginMessage);
//             this.doWait(timeout, loginFuture);
//             Message result = loginFuture.get();
//             if (!ResultCodes.isSuccess(result.getCode()))
//                 throw new TunnelWriteException(new ValidatorFailException(NetResponseCode.VALIDATOR_FAIL));
//         }
//     }
//
//     @Override
//     public void resend(ResendMessage<UID> message) {
//         tunnel.resend(message);
//     }
//
//     @Override
//     public StageableFuture<Void> close() {
//         return tunnel.close();
//     }
//
//     @Override
//     public boolean isClosed() {
//         return tunnel.isClosed();
//     }
//
//     private static NextTaskAction action = new NextTaskAction();
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
// }
