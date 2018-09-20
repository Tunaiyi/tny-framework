// package com.tny.game.net.netty;
//
// import com.tny.game.common.concurrent.StageableFuture;
// import com.tny.game.common.result.ResultCodes;
// import com.tny.game.common.utils.URL;
// import com.tny.game.net.base.NetResponseCode;
// import com.tny.game.net.exception.*;
// import com.tny.game.net.transport.*;
// import com.tny.game.net.transport.message.Message;
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
// public class NettyClient<UID> implements Communicator<UID> {
//
//     public static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
//
//     private NettyConnector<UID> connector;
//
//     private NetSession<UID> seesion;
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
//         return this.seesion.getUid();
//     }
//
//     @Override
//     public String getUserType() {
//         return this.seesion.getUserType();
//     }
//
//     @Override
//     public Certificate<UID> getCertificate() {
//         return null;
//     }
//
//     @Override
//     public boolean isLogin() {
//         return false;
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
//                         Tunnel<UID> tunnel = connector.connect(url);
//                         login(true, tunnel);
//                     }
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
//     private void login(boolean relogin, Tunnel<UID> tunnel) throws InterruptedException, ExecutionException, TunnelWriteException, TimeoutException {
//         if (loginContentCreator != null) {
//             MessageContent<UID> loginMessage = loginContentCreator.apply(relogin, tunnel);
//             StageableFuture<Message<UID>> loginFuture = loginMessage.messageFuture(this.loginTimeout + 1000L);
//             // tunnel.send(loginMessage);
//             // this.doWait(timeout, loginFuture);
//             Message result = loginFuture.get();
//             if (!ResultCodes.isSuccess(result.getCode()))
//                 throw new TunnelWriteException(new ValidatorFailException(NetResponseCode.VALIDATOR_FAIL));
//         }
//     }
//
//     @Override
//     public void resend(ResendMessage<UID> message) {
//         seesion.resend(message);
//     }
//
//     @Override
//     public StageableFuture<Void> close() {
//         return seesion.close();
//     }
//
//     @Override
//     public boolean isClosed() {
//         return seesion.isClosed();
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
