package com.tny.game.net.common.session;

import com.google.common.collect.Range;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.event.BindP1EventBus;
import com.tny.game.common.event.EventBuses;
import com.tny.game.common.utils.Logs;
import com.tny.game.common.utils.ObjectAide;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.exception.RemotingException;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageBuilder;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.LoginCertificate;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.ResendMessage;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.SessionAide;
import com.tny.game.net.session.SessionEventsBox;
import com.tny.game.net.session.SessionState;
import com.tny.game.net.session.event.SessionEvent.SessionEventType;
import com.tny.game.net.session.event.SessionInputEvent;
import com.tny.game.net.session.event.SessionInputEventHandler;
import com.tny.game.net.session.event.SessionOutputEvent;
import com.tny.game.net.session.event.SessionOutputEventHandler;
import com.tny.game.net.session.event.SessionReceiveEvent;
import com.tny.game.net.session.event.SessionResendEvent;
import com.tny.game.net.session.event.SessionSendEvent;
import com.tny.game.net.session.holder.listener.SessionListener;
import com.tny.game.net.tunnel.NetTunnel;
import com.tny.game.net.tunnel.Tunnel;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.*;

/**
 * 通用Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class CommonSession<UID> implements NetSession<UID> {

    private static final BindP1EventBus<SessionListener, Session, Tunnel> ON_ONLINE =
            EventBuses.of(SessionListener.class, SessionListener::onOnline);

    private static final BindP1EventBus<SessionListener, Session, Tunnel> ON_OFFLINE =
            EventBuses.of(SessionListener.class, SessionListener::onOffline);

    private static final BindP1EventBus<SessionListener, Session, Tunnel> ON_CLOSE =
            EventBuses.of(SessionListener.class, SessionListener::onClose);

    public static final Logger LOGGER = getLogger(CommonSession.class);

    private volatile long id;

    private volatile SessionEventsBox<UID> eventBox;

    private volatile long offlineTime;

    private volatile long lastReceiveTime;

    private volatile NetTunnel<UID> currentTunnel;

    /* 认证 */
    private LoginCertificate<UID> certificate;

    /* 消息工厂 */
    protected MessageBuilderFactory<UID> messageBuilderFactory;

    /* 消息校验码生成器 */
    private MessageSignGenerator<UID> messageSignGenerator;

    private AtomicInteger sessionState = new AtomicInteger(SessionState.ONLINE.getId());

    private SessionInputEventHandler<UID, NetSession<UID>> inputEventHandler;

    private SessionOutputEventHandler<UID, NetSession<UID>> outputEventHandler;

    @SuppressWarnings("unchecked")
    public CommonSession(NetTunnel<UID> tunnel, UID unloginUID, MessageBuilderFactory<UID> messageBuilderFactory, SessionOutputEventHandler<UID, NetSession<UID>> outputEventHandler, SessionInputEventHandler<UID, NetSession<UID>> inputEventHandler, MessageSignGenerator<UID> messageSignGenerator, int cacheMessageSize) {
        this.id = SessionAide.newSessionID();
        this.certificate = LoginCertificate.createUnLogin(unloginUID);
        this.messageSignGenerator = messageSignGenerator;
        this.messageBuilderFactory = messageBuilderFactory;
        this.outputEventHandler = outputEventHandler;
        this.inputEventHandler = inputEventHandler;
        this.eventBox = SessionEventsBox.create(this, cacheMessageSize);
        this.currentTunnel = tunnel;
        this.currentTunnel.bind(this);
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public UID getUID() {
        return certificate.getUserID();
    }

    @Override
    public String getUserGroup() {
        return certificate.getUserGroup();
    }

    @Override
    public Instant getLoginAt() {
        return certificate.getLoginAt();
    }

    @Override
    public boolean isLogin() {
        return this.certificate.isLogin();
    }

    @Override
    public Attributes attributes() {
        return this.eventBox.attributes();
    }

    @Override
    public LoginCertificate<UID> getCertificate() {
        return certificate;
    }

    @Override
    public void login(LoginCertificate<UID> certificate) throws ValidatorFailException {
        if (!certificate.isLogin())
            throw new ValidatorFailException("Certificate unlogin");
        this.certificate = certificate;
        ON_ONLINE.notify(this, this.currentTunnel);
    }

    @Override
    public boolean relogin(NetSession<UID> cerSession) {
        LoginCertificate<UID> certificate = cerSession.getCertificate();
        if (this.certificate.getID() != certificate.getID()) {
            LOGGER.warn("重登certificate ID [{}] 与Session certificate ID[{}] 不同! 重登失败!", certificate.getID(), this.certificate.getID());
            return false;
        }
        int stateID = this.sessionState.get();
        if (stateID != SessionState.ONLINE.getId() && stateID != SessionState.OFFLINE.getId()) {
            LOGGER.warn("session 状态为 {} 无法重登", SessionState.INVALID);
            return false;
        }
        if (this.sessionState.compareAndSet(stateID, SessionState.ONLINE.getId())) {
            synchronized (this) {
                this.certificate = certificate;
                CommonSession<UID> session = (CommonSession<UID>) cerSession;
                Tunnel<UID> old = this.currentTunnel;
                this.currentTunnel = session.getCurrentTunnel();
                this.currentTunnel.bind(this);
                this.eventBox.accept(session.eventBox);
                old.close();
            }
            ON_ONLINE.notify(this, this.currentTunnel);
            return true;
        }
        LOGGER.warn("session {} 状态已经改变为 {} ,重登失败", stateID, this.sessionState.get());
        return false;
    }

    @Override
    public void offlineIfCurrent(Tunnel<UID> tunnel) {
        if (!isOnline())
            return;
        Tunnel<UID> offline = this.currentTunnel;
        if (offline != tunnel)
            return;
        synchronized (this) {
            if (this.currentTunnel != offline)
                return;
            if (this.sessionState.compareAndSet(SessionState.ONLINE.getId(), SessionState.OFFLINE.getId())) {
                this.offlineTime = System.currentTimeMillis();
                offline.close();
                ON_OFFLINE.notify(this, offline);
            }
        }
    }

    @Override
    public void offline() {
        // this.sessionState
        if (this.sessionState.compareAndSet(SessionState.ONLINE.getId(), SessionState.OFFLINE.getId())) {
            Tunnel<UID> tunnel = this.currentTunnel;
            this.offlineTime = System.currentTimeMillis();
            tunnel.close();
            ON_OFFLINE.notify(this, this.currentTunnel);
        }
    }

    @Override
    public boolean close() {
        int state = this.sessionState.get();
        while (state != SessionState.INVALID.getId()) {
            if (this.sessionState.compareAndSet(state, SessionState.INVALID.getId())) {
                Tunnel<UID> tunnel = this.currentTunnel;
                if (tunnel != null && tunnel.getSession() == this)
                    tunnel.close();
                if (state == SessionState.ONLINE.getId())
                    ON_OFFLINE.notify(this, tunnel);
                ON_CLOSE.notify(this, tunnel);
                return true;
            } else {
                state = this.sessionState.get();
            }
        }
        return false;
    }


    @Override
    public void receive(Message<UID> message) {
        receive(this.currentTunnel, message);
    }

    @Override
    public void receive(Tunnel<UID> tunnel, Message<UID> message) {
        try {
            NetLogger.logReceive(this, message);
            MessageMode mode = message.getMode();
            if (tunnel.getReceiveExcludes().contains(mode)) {
                LOGGER.debug("{}无法接收 {} 信息[{}]", tunnel, mode, message.getProtocol());
                if (mode == MessageMode.REQUEST)
                    send(MessageContent.toResponse(message, CoreResponseCode.NO_RECEIVE_MODE));
                return;
            }
            switch (mode) {
                case PING:
                    if (tunnel instanceof NetTunnel)
                        ((NetTunnel) tunnel).pong();
                    return;
                case PONG:
                    return;
                case PUSH:
                case REQUEST:
                case RESPONSE:
                    MessageFuture<?> future = null;
                    if (MessageMode.RESPONSE.isMode(message)) {
                        int toMessage = message.getToMessage();
                        future = MessageFuture.getFuture(this.getID(), toMessage);
                    }
                    this.eventBox.addInputEvent(receiveEvent(getTunnel(tunnel), message, future));
                    inputEventHandler.onInput(this);
            }
        } finally {
            this.lastReceiveTime = System.currentTimeMillis();
        }
    }

    @Override
    public void send(MessageContent content) throws RemotingException {
        send(this.currentTunnel, content);
    }

    @Override
    public void send(Tunnel<UID> tunnel, MessageContent<?> content) {
        Message<UID> message = null;
        try {
            MessageMode mode = content.getMode();
            if (tunnel.getSendExcludes().contains(mode)) {
                LOGGER.debug("{}无法发送 {} 信息[{}]", tunnel, mode, content.getProtocol());
                return;
            }
            tunnel = getTunnel(tunnel);
            MessageBuilder<UID> builder = messageBuilderFactory.newMessageBuilder()
                    .setContent(content)
                    .setSignGenerator(this.messageSignGenerator)
                    .setTime(System.currentTimeMillis())
                    .setTunnel(tunnel);
            final SessionEventsBox eventBox = this.eventBox;
            SessionSendEvent<UID> event;
            synchronized (eventBox) {
                message = builder.setID(eventBox.allotMessageID())
                        .build();
                NetLogger.logSend(this, message);
                event = sendEvent(tunnel, message, content);
                eventBox.addOutputEvent(event);
                if (content.hasMessageFuture()) {
                    MessageFuture.putFuture(this, message, content
                            .getMessageFuture());
                }
                outputEventHandler.onOutput(this);
            }
            if (content.isWaitForSent())
                event.waitForSend(content.getSentTimeout());
        } catch (Throwable e) {
            LOGGER.error("send response exception", e);
            if (content.isWaitForSent())
                throw new RemotingException(Logs.format("{} send {} failed", this, message));
        }
    }

    @Override
    public void resend(ResendMessage message) {
        this.resend(this.currentTunnel, message);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void resend(Tunnel<UID> tunnel, ResendMessage message) {
        this.eventBox.addOutputEvent(new SessionResendEvent<>(getTunnel(tunnel), message.getResendRange(), ObjectAide.as(message.getSendFuture())));
    }

    @Override
    public boolean hasInputEvent() {
        return this.eventBox.hasInputEvent();
    }

    @Override
    public boolean hasOutputEvent() {
        return this.eventBox.hasOutputEvent();
    }

    private Tunnel<UID> getTunnel(Tunnel<UID> tunnel) {
        return tunnel == null ? this.currentTunnel : tunnel;
    }

    @Override
    public List<SessionSendEvent<UID>> getHandledSendEvents(Range<Integer> range) {
        return this.eventBox.getHandledSendEvents(range);
    }

    @Override
    public SessionSendEvent<UID> getHandledSendEventByToID(int toMessageID) {
        return this.eventBox.getHandledSendEventByToID(toMessageID);
    }

    @Override
    public SessionInputEvent<UID> pollInputEvent() {
        return this.eventBox.pollInputEvent();
    }

    @Override
    public SessionOutputEvent<UID> pollOutputEvent() {
        return this.eventBox.pollOutputEvent();
    }

    @Override
    public boolean isOnline() {
        return this.sessionState.get() == SessionState.ONLINE.getId();
    }

    @Override
    public boolean isOffline() {
        return this.sessionState.get() == SessionState.OFFLINE.getId();
    }

    @Override
    public boolean isClosed() {
        return this.sessionState.get() == SessionState.INVALID.getId();
    }

    @Override
    public long getOfflineTime() {
        return offlineTime;
    }

    @Override
    public long getLastReceiveTime() {
        return lastReceiveTime;
    }

    @Override
    public NetTunnel<UID> getCurrentTunnel() {
        return currentTunnel;
    }

    // protected void closeTunnel(String reason) {
    //     Tunnel<UID> session = this.session;
    //     if (session != null && session.isConnected()) {
    //         if (LOGGER.isInfoEnabled())
    //             LOGGER.info("Session主动断开## Tunnel {} ##通道 {}", reason, session);
    //         session.close();
    //     }
    // }

    protected MessageSignGenerator<UID> getMessageSignGenerator() {
        return messageSignGenerator;
    }

    private SessionReceiveEvent receiveEvent(Tunnel<UID> tunnel, Message<UID> message, MessageFuture<?> future) {
        return new SessionReceiveEvent<>(tunnel, message, SessionEventType.MESSAGE, future);
    }

    private SessionSendEvent<UID> sendEvent(Tunnel<UID> tunnel, Message<UID> message, MessageContent content) {
        return new SessionSendEvent<>(tunnel, message, content.isWaitForSent(), ObjectAide.as(content.getSendFailure()));
    }

    @Override
    public String toString() {
        return "Session [" + this.getUserGroup() + "." + this.getUID() + " | " + this.currentTunnel + "]";
    }
    // protected abstract void connect() throws RemotingException;

    // @Override
    // public void pollInputEvent() {
    //     int time = this.sendNumPerTime;
    //     try {
    //         while (time < 0 || time > 0 || this.outputEventQueue.isEmpty()) {
    //             try {
    //                 MessageOrder order = this.outputEventQueue.poll();
    //                 if (order == null)
    //                     break;
    //                 switch (order.getOrderType()) {
    //                     case SEND:
    //                         this.addHandledOutputEvent((SessionSendEvent) order);
    //                         break;
    //                     case RESEND:
    //                         break;
    //                 }
    //             } catch (Throwable e) {
    //                 LOGGER.error("processSendMessages {} ", e);
    //             }
    //         }
    //     } finally {
    //         this.writeMessageLock.set(false);
    //     }
    //     if (this.hasInputEvent())
    //         this.submitToWrite();
    // }
    //
    // @Override
    // public void processReceiveMessage(MessageDispatcher dispatcher) {
    //     int time = this.receiveNamPerTime;
    //     try {
    //         while (time < 0 || time > 0 || this.receiveCommandQueue.isEmpty()) {
    //             try {
    //                 NetMessage message = this.receiveCommandQueue.poll();
    //                 if (message == null)
    //                     break;
    //                 int toMessage = message.getToMessage();
    //                 if (toMessage > 0 && this.futureHolder != null) {
    //                     MessageFuture<?> future = this.futureHolder.takeFuture(message.getToMessage());
    //                     if (future != null)
    //                         future.setResponse(message);
    //                 }
    //                 dispatcher.dispatch(message, this);
    //             } catch (Throwable e) {
    //                 LOGGER.error("processSendMessages {} ", e);
    //             }
    //         }
    //     } finally {
    //         this.readMessageLock.set(false);
    //     }
    //     if (this.hasOutputEvent())
    //         this.submitToRead();
    // }
}
