// package com.tny.game.net.transport;
//
// import com.tny.game.com.tny.game.common.context.Attributes;
// import com.tny.game.net.base.NetLogger;
// import com.tny.game.net.exception.NetException;
// import com.tny.game.net.message.*;
// import org.slf4j.*;
//
// import java.net.InetSocketAddress;
// import java.rmi.server.UID;
//
// import static com.tny.game.com.tny.game.common.utils.ObjectAide.*;
//
// /**
//  * <p>
//  *
//  * @author: Kun Yang
//  * @date: 2018-10-23 12:16
//  */
// public class TunnelEndpiont extends AbstractEndpoint<UID> implements Tunnel<UID>, NetEndpoint<UID> {
//
//     public static final Logger LOGGER = LoggerFactory.getLogger(TunnelEndpiont.class);
//
//     private Tunnel<UID> tunnel;
//
//     private NetEndpoint<UID> endpoint;
//
//     protected TunnelEndpiont(Certificate<UID> certificate) {
//         super(certificate, MessageIdCreator.TUNNEL_SENDER_MESSAGE_ID_MARK);
//     }
//
//     @Override
//     public long getId() {
//         return tunnel.getId();
//     }
//
//     @Override
//     public long getAccessId() {
//         return tunnel.getAccessId();
//     }
//
//     @Override
//     public TunnelMode getMode() {
//         return tunnel.getMode();
//     }
//
//     @Override
//     public Attributes attributes() {
//         return tunnel.attributes();
//     }
//
//     @Override
//     public void heartbeat() {
//
//     }
//
//     @Override
//     public boolean isAvailable() {
//         return tunnel.isAvailable();
//     }
//
//     @Override
//     public boolean isAlive() {
//         return tunnel.isAlive();
//     }
//
//     @Override
//     public TunnelState getState() {
//         return tunnel.getState();
//     }
//
//     @Override
//     public InetSocketAddress getRemoteAddress() {
//         return tunnel.getRemoteAddress();
//     }
//
//     @Override
//     public InetSocketAddress getLocalAddress() {
//         return tunnel.getLocalAddress();
//     }
//
//     @Override
//     public UID getUserId() {
//         return tunnel.getUserId();
//     }
//
//     @Override
//     public String getUserType() {
//         return tunnel.getUserType();
//     }
//
//     @Override
//     public Certificate<UID> getCertificate() {
//         return tunnel.getCertificate();
//     }
//
//     @Override
//     public boolean isLogin() {
//         return tunnel.isLogin();
//     }
//
//     @Override
//     public boolean isClosed() {
//         return tunnel.isClosed();
//     }
//
//     @Override
//     public void close() {
//         tunnel.close();
//     }
//
//     @Override
//     public SendContext<UID> write(Message<UID> message, MessageContext<UID> context, long waitForSendTimeout, WriteCallback<UID> callback) throws NetException {
//         return tunnel.write(message, context, waitForSendTimeout, callback);
//     }
//
//     @Override
//     public SendContext<UID> sendAsyn(MessageSubject subject, MessageContext<UID> context) {
//         try {
//             NetEndpoint<UID> endpoint = this.endpoint;
//             if (endpoint != null)
//                 return endpoint.sendAsyn(this, subject, context);
//             else
//                 return send(subject, context, 0);
//         } catch (Throwable e) {
//             return ifNull(context, EmptySendContext.empty());
//         }
//     }
//
//     @Override
//     public SendContext<UID> sendSync(MessageSubject subject, MessageContext<UID> context, long timeout) {
//         NetEndpoint<UID> endpoint = this.endpoint;
//         if (endpoint != null)
//             return endpoint.sendSync(this, subject, context, timeout);
//         else
//             return send(subject, context, timeout);
//     }
//
//     @Override
//     protected NetTunnel<UID> selectTunnel(MessageSubject subject, MessageContext<UID> messageContext) {
//         return null;
//     }
//
//     private SendContext<UID> send(MessageSubject subject, MessageContext<UID> context, long waitForSendTimeout) throws NetException {
//         try {
//             Message<UID> message = this.createMessage(subject, context);
//             return write(message, context, waitForSendTimeout, this);
//         } catch (RuntimeException e) {
//             LOGGER.error("", e);
//             this.completeExceptionally(context, e);
//             throw e;
//         } catch (Exception e) {
//             LOGGER.error("", e);
//             this.completeExceptionally(context, e);
//             throw new NetException(e);
//         }
//     }
//
//     @Override
//     public boolean receive(Tunnel<UID> tunnel, Message<UID> message) {
//         NetLogger.logReceive(this, message);
//         MessageMode mode = message.getMode();
//         switch (mode) {
//             case PUSH:
//             case REQUEST:
//             case RESPONSE:
//                 try {
//                     NetEndpoint<UID> endpoint = this.endpoint;
//                     if (endpoint != null)
//                         return endpoint.receive(tunnel, message);
//                 } finally {
//                     if (mode == MessageMode.RESPONSE)
//                         this.callbackFuture(message);
//                 }
//                 return true;
//             default:
//                 break;
//         }
//         return true;
//     }
//
//     @Override
//     public SendContext<UID> sendAsyn(Tunnel<UID> tunnel, MessageSubject subject, MessageContext<UID> messageContext) {
//         return null;
//     }
//
//     @Override
//     public SendContext<UID> sendSync(Tunnel<UID> tunnel, MessageSubject subject, MessageContext<UID> messageContext, long timeout) throws NetException {
//         return null;
//     }
//
//     @Override
//     public void onDisable(NetTunnel<UID> tunnel) {
//
//     }
// }
