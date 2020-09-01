// package com.tny.game.net.transport;
//
// import com.tny.game.com.tny.game.common.context.*;
// import com.tny.game.com.tny.game.common.event.BindVoidEventBus;
// import com.tny.game.net.base.NetResultCode;
// import com.tny.game.net.exception.*;
// import com.tny.game.net.message.*;
// import com.tny.game.net.transport.listener.*;
// import org.slf4j.*;
//
// import java.util.Objects;
// import java.util.function.Consumer;
//
// import static com.tny.game.com.tny.game.common.utils.StringAide.*;
//
// /**
//  * 抽象通道
//  * Created by Kun Yang on 2017/3/26.
//  */
// public abstract class AbstractTransport<UID> implements Transport<UID> {
//
//     public static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransport.class);
//
//     protected volatile TunnelState state = TunnelState.INIT;
//
//     private final long id;
//
//     private long accessId;
//
//     /* 附加属性 */
//     private Attributes attributes;
//
//     private volatile NetEndpoint<UID> endpoint;
//
//     protected AbstractTransport() {
//         this.id = NetAide.newTransportId();
//     }
//
//     @Override
//     public long getAccessId() {
//         return accessId;
//     }
//
//     @Override
//     public long getId() {
//         return id;
//     }
//
//     public boolean isAlive() {
//         return state == TunnelState.ALIVE;
//     }
//
//     @Override
//     public TunnelState getState() {
//         return state;
//     }
//
//     public void setAccessId(long accessId) {
//         this.accessId = accessId;
//     }
//
//
//     @Override
//     public SendContext<UID> write(Message<UID> message, MessageContext<UID> context, long waitForSendTimeout, WriteCallback<UID> callback) throws NetException {
//         try {
//             return doWrite(message, context, waitForSendTimeout, callback);
//         } catch (Throwable e) {
//             LOGGER.error("", e);
//             this.completeExceptionally(context, e);
//             throw e;
//         }
//     }
//
//     protected abstract SendContext<UID> doWrite(Message<UID> message, MessageContext<UID> context, long waitForSendTimeout, WriteCallback<UID> callback) throws NetException;
//
//     @Override
//     public void ping() {
//         this.write(DetectMessage.ping(), null, 0, null);
//     }
//
//     @Override
//     public void pong() {
//         this.write(DetectMessage.pong(), null, 0, null);
//     }
//
//     @Override
//     public Attributes attributes() {
//         if (this.attributes != null)
//             return this.attributes;
//         synchronized (this) {
//             if (this.attributes != null)
//                 return this.attributes;
//             return this.attributes = ContextAttributes.create();
//         }
//     }
//
//     @Override
//     public boolean isBind() {
//         return this.endpoint != null;
//     }
//
//     @Override
//     public boolean bind(NetEndpoint<UID> endpoint) {
//         if (this.endpoint != null)
//             return false;
//         synchronized (this) {
//             if (this.endpoint != null)
//                 return false;
//             if (this.getCertificate().isSameCertificate(endpoint.getCertificate())) {
//                 this.endpoint = endpoint;
//                 return true;
//             }
//         }
//
//         return false;
//     }
//
//     @Override
//     public boolean open() {
//         if (this.isAvailable())
//             return true;
//         if (this.isClosed())
//             return false;
//         synchronized (this) {
//             if (this.isAvailable())
//                 return true;
//             if (this.isClosed())
//                 return false;
//             if (!this.onOpen())
//                 return false;
//         }
//         this.state = TunnelState.ALIVE;
//         this.openEvent().notify(this);
//         return true;
//     }
//
//     @Override
//     public void disconnect() {
//         synchronized (this) {
//             if (state == TunnelState.CLOSE || state == TunnelState.UNALIVE)
//                 return;
//             this.doDisconnect();
//             this.state = TunnelState.UNALIVE;
//         }
//         this.unaliveEvent().notify(this);
//         NetEndpoint<UID> endpoint = this.endpoint;
//         if (endpoint != null)
//             endpoint.onDisable(this);
//     }
//
//     @Override
//     public void close() {
//         if (state == TunnelState.CLOSE)
//             return;
//         synchronized (this) {
//             if (state == TunnelState.CLOSE)
//                 return;
//             this.onClose();
//             this.state = TunnelState.CLOSE;
//         }
//         this.closeEvent().notify(this);
//         NetEndpoint<UID> endpoint = this.endpoint;
//         if (endpoint != null)
//             endpoint.onDisable(this);
//     }
//
//     protected abstract void onClose();
//
//     protected abstract boolean onOpen();
//
//     protected abstract void doDisconnect();
//
//     protected BindVoidEventBus<TunnelAuthenticateListener, Tunnel> authenticateEvent() {
//         return TunnelEvents.ON_AUTHENTICATE;
//     }
//
//     protected BindVoidEventBus<TunnelOpenListener, Tunnel> openEvent() {
//         return TunnelEvents.ON_ACTIVATE;
//     }
//
//     protected BindVoidEventBus<TunnelUnaliveListener, Tunnel> unaliveEvent() {
//         return TunnelEvents.ON_UNACTIVATED;
//     }
//
//     protected BindVoidEventBus<TunnelCloseListener, Tunnel> closeEvent() {
//         return TunnelEvents.ON_CLOSE;
//     }
//
//     // @Override
//     // public Optional<Endpoint<UID>> getBindEndpoint() {
//     //     return Optional.ofNullable(this.endpoint);
//     // }
//
//     protected void onNotify(Consumer<NetEndpoint<UID>> consumer) {
//         NetEndpoint<UID> endpoint = this.endpoint;
//         if (endpoint != null) {
//             try {
//                 consumer.accept(endpoint);
//             } catch (Throwable e) {
//                 LOGGER.error("", e);
//             }
//         }
//     }
//
//     @Override
//     public boolean equals(Object o) {
//         if (this == o) return true;
//         if (!(o instanceof AbstractTransport)) return false;
//         AbstractTransport<?> that = (AbstractTransport<?>) o;
//         return id == that.id;
//     }
//
//     @Override
//     public int hashCode() {
//         return Objects.hash(id);
//     }
//
// }
