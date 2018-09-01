package com.tny.game.net.session;

import com.google.common.collect.Range;
import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.SessionEvent.SessionEventType;
import com.tny.game.net.tunnel.*;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.*;

/**
 * 通用Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class SingleTunnelSession<UID> implements NetSession<UID> {

    public static final Logger LOGGER = getLogger(SingleTunnelSession.class);

    private volatile long id;

    private volatile SessionEventsBox<UID> eventBox;

    private volatile long offlineTime;

    private volatile NetTunnel<UID> currentTunnel;

    /* 认证 */
    private NetCertificate<UID> certificate;

    private volatile int messageIDCounter = 0;

    private AtomicInteger sessionState = new AtomicInteger(SessionState.ONLINE.getId());

    private SessionInputEventHandler<UID, NetSession<UID>> inputEventHandler;

    private SessionOutputEventHandler<UID, NetSession<UID>> outputEventHandler;

    @SuppressWarnings("unchecked")
    public SingleTunnelSession(NetTunnel<UID> tunnel, UID unloginUID, SessionInputEventHandler<UID, NetSession<UID>> inputEventHandler, SessionOutputEventHandler<UID, NetSession<UID>> outputEventHandler, int cacheMessageSize) {
        this.init(tunnel, unloginUID, SessionEventsBox.create(cacheMessageSize), inputEventHandler, outputEventHandler);
    }

    private void init(NetTunnel<UID> tunnel, UID unloginUID, SessionEventsBox<UID> eventBox, SessionInputEventHandler<UID, NetSession<UID>> inputEventHandler, SessionOutputEventHandler<UID, NetSession<UID>> outputEventHandler) {
        this.id = SessionAide.newSessionID();
        this.certificate = NetCertificate.createUnLogin(unloginUID);
        this.outputEventHandler = outputEventHandler;
        this.inputEventHandler = inputEventHandler;
        this.offlineTime = 0;
        this.currentTunnel = tunnel;
        this.currentTunnel.bind(this);
        this.eventBox = eventBox;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public UID getUid() {
        return getCertificate().getUserId();
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
    public NetCertificate<UID> getCertificate() {
        return certificate;
    }

    @Override
    public void login(NetCertificate<UID> certificate) throws ValidatorFailException {
        int stateID = this.sessionState.get();
        if (stateID == SessionState.CLOSE.getId())
            throw new ValidatorFailException(NetResponseCode.SESSION_LOSS);
        if (stateID == SessionState.OFFLINE.getId())
            throw new ValidatorFailException("session is offline");
        if (this.certificate.isLogin())
            throw new ValidatorFailException("session is login");
        if (!certificate.isLogin())
            throw new ValidatorFailException("Certificate unlogin");
        this.certificate = certificate;
        this.offlineTime = 0;
        ON_ONLINE.notify(this, this.currentTunnel);
    }

    @Override
    public void mergeSession(NetSession<UID> newSession) throws ValidatorFailException {
        if (this == newSession)
            return;
        Throws.checkNotNull(newSession, "newSession is null");
        NetCertificate<UID> newCertificate = newSession.getCertificate();
        if (!newCertificate.isLogin()) {
            throw new ValidatorFailException(NetResponseCode.UNLOGIN);
        }
        if (!certificate.isSameCertificate(newCertificate)) { // 是否是同一个授权
            throw new ValidatorFailException(StringAide.format("Certificate new [{}] 与 old [{}] 不同", newCertificate, this.certificate));
        }
        while (true) {
            int stateID = this.sessionState.get();
            if (stateID == SessionState.CLOSE.getId()) { // 判断 session 状态是否可以重登
                throw new ValidatorFailException(NetResponseCode.SESSION_LOSS);
            }
            if (this.sessionState.compareAndSet(stateID, SessionState.ONLINE.getId())) { // 尝试执行重登切换
                NetTunnel<UID> current = doMergeSession(newCertificate, newSession);
                ON_ONLINE.notify(this, current);
                return;
            }
        }
    }

    @Override
    public boolean offlineIfCurrent(Tunnel<UID> tunnel) {
        if (this.currentTunnel != tunnel)
            return false;
        synchronized (this) {
            if (this.currentTunnel != tunnel)
                return false;
            if (this.sessionState.compareAndSet(SessionState.ONLINE.getId(), SessionState.OFFLINE.getId())) {
                doOffline(this.currentTunnel);
                return true;
            } else {
                return false;
            }
        }
    }

    void doOffline(Tunnel<UID> tunnel) {
        this.offlineTime = System.currentTimeMillis();
        tunnel.close();
        ON_OFFLINE.notify(this, tunnel);
    }

    @Override
    public void offline() {
        // this.sessionState
        if (this.sessionState.compareAndSet(SessionState.ONLINE.getId(), SessionState.OFFLINE.getId())) {
            doOffline(this.currentTunnel);
        }
    }

    @Override
    public StageableFuture<Void> close() {
        int state = this.sessionState.get();
        while (state != SessionState.CLOSE.getId()) {
            onTryClose();
            if (this.sessionState.compareAndSet(state, SessionState.CLOSE.getId())) {
                return doClose(state);
            } else {
                state = this.sessionState.get();
            }
        }
        return CommonStageableFuture.completedFuture(null);
    }

    void onTryClose() {
    }


    StageableFuture<Void> doClose(int state) {
        StageableFuture<Void> future = null;
        Tunnel<UID> tunnel = this.currentTunnel;
        if (tunnel != null && tunnel.getSession() == this)
            future = tunnel.close();
        if (state == SessionState.ONLINE.getId())
            ON_OFFLINE.notify(this, tunnel);
        ON_CLOSE.notify(this, tunnel);
        return future;
    }

    @Override
    public void receive(Message<UID> message) {
        receive(this.currentTunnel, message);
    }

    @Override
    public void receive(NetTunnel<UID> tunnel, Message<UID> message) {
        NetLogger.logReceive(this, message);
        MessageMode mode = message.getMode();
        int state = this.sessionState.get();
        if (state == SessionState.CLOSE.getId()) {
            LOGGER.warn("Session [{}] 已经关闭, 无法接受消息", this);
            return;
        }
        if (tunnel.isReceiveExclude(mode)) {
            LOGGER.debug("{}无法接收 {} 信息[{}]", tunnel, mode, message.getProtocol());
            if (mode == MessageMode.REQUEST)
                send(tunnel, MessageContent.toResponse(message, NetResponseCode.NO_RECEIVE_MODE));
            return;
        }
        switch (mode) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                RespondMessageFuture<UID> future = null;
                if (MessageMode.RESPONSE.isMode(message)) {
                    MessageHeader header = message.getHeader();
                    int toMessage = header.getToMessage();
                    future = RespondMessageFutures.pollFuture(this.getId(), toMessage);
                }
                this.eventBox.addInputEvent(receiveEvent(thenNullGetCurrent(tunnel), message, future));
                inputEventHandler.onInput(this);
                return;
            case PING:
                tunnel.pong();
                return;
            default:
        }
    }

    @Override
    public void send(MessageContent<UID> content) throws RemotingException {
        send(this.currentTunnel, content);
    }


    @Override
    public void send(NetTunnel<UID> tunnel, MessageContent<UID> content) {
        try {
            int state = this.sessionState.get();
            if (state == SessionState.CLOSE.getId())
                throw new SessionException("Session [{}] 已经关闭, 无法发送", this);
            MessageMode mode = content.getMode();
            if (tunnel.isSendExclude(mode))
                throw new SessionException("Tunnel [{}] 无法发送 {} 信息[{}]", tunnel, mode, content.getProtocol());
            final SessionEventsBox<UID> eventBox = this.eventBox;
            SessionSendEvent<UID> event = sendEvent(tunnel, content);
            eventBox.addOutputEvent(event);
            outputEventHandler.onOutput(this);
            if (content.isWaitForSent())
                content.sendFuture().get(content.getWaitForSentTimeout(), TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            LOGGER.warn("send exception: {} | message: {}", e.getClass(), e.getMessage());
            if (content.isHasFuture())
                content.sendFailed(e);
            if (content.isWaitForSent())
                throw new RemotingException(Logs.format("{} send {} failed", this, content), e);
        }
    }

    @Override
    public void resend(ResendMessage<UID> message) {
        this.resend(this.currentTunnel, message);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void resend(NetTunnel<UID> tunnel, ResendMessage<UID> message) {
        try {
            int state = this.sessionState.get();
            if (state == SessionState.CLOSE.getId())
                throw new SessionException("Session [{}] 已经关闭, 无法重发", this);
            if (state == SessionState.OFFLINE.getId())
                throw new SessionException("Session [{}] 已经离线, 无法重发", this);
            this.eventBox.addOutputEvent(new SessionResendEvent<>(thenNullGetCurrent(tunnel), message));
        } catch (Throwable e) {
            message.resendFailed(e);
            LOGGER.warn("resend exception: {} | message: {}", e.getClass(), e.getMessage());
        }
    }

    @Override
    public boolean isHasInputEvent() {
        return this.eventBox.hasInputEvent();
    }

    @Override
    public boolean isHasOutputEvent() {
        return this.eventBox.hasOutputEvent();
    }

    @Override
    public int getInputEventSize() {
        return eventBox.getInputEventSize();
    }

    @Override
    public int getOutputEventSize() {
        return eventBox.getOutputEventSize();
    }

    private NetTunnel<UID> thenNullGetCurrent(NetTunnel<UID> tunnel) {
        return tunnel == null ? this.currentTunnel : tunnel;
    }

    @Override
    public List<Message<UID>> getSentMessages(Range<Integer> range) {
        return this.eventBox.getSentMessage(range);
    }

    @Override
    public Message<UID> getSentMessageByToID(int toMessageID) {
        return this.eventBox.getSentMessageByToID(toMessageID);
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
        return this.sessionState.get() == SessionState.CLOSE.getId();
    }

    @Override
    public long getOfflineTime() {
        return offlineTime;
    }

    @Override
    public NetTunnel<UID> getCurrentTunnel() {
        return currentTunnel;
    }

    @Override
    public void write(SessionSendEvent<UID> event) throws TunnelWriteException {
        synchronized (this) {
            Optional<NetTunnel<UID>> tunnelOpt = event.getTunnel();
            if (tunnelOpt.isPresent()) {
                try {
                    NetTunnel<UID> tunnel = tunnelOpt.get();
                    Message<UID> message = tunnel.getMessageBuilderFactory().newBuilder()
                            .setID(++messageIDCounter)
                            .setSessionID(this.getId())
                            .setContent(event.getContent())
                            .setTunnel(tunnel)
                            .build();
                    eventBox.addSentMessage(message);
                    tunnel.write(message, event);
                } catch (Exception e) {
                    LOGGER.error("", e);
                    event.sendFail(e);
                    throw e;
                }
            } else {
                TunnelException exception = new TunnelException("tunnel is close");
                event.sendFail(exception);
                throw exception;
            }
        }
    }

    NetTunnel<UID> doMergeSession(NetCertificate<UID> newCertificate, NetSession<UID> newSession) {
        synchronized (this) {
            this.certificate = newCertificate;
            this.offlineTime = 0;
            NetTunnel<UID> newTunnel = newSession.getCurrentTunnel();
            NetTunnel<UID> oldTunnel = this.currentTunnel;
            this.currentTunnel = newTunnel;
            this.currentTunnel.bind(this); //新Tunnel绑定当前 session
            if (newSession instanceof SingleTunnelSession) { // 合并 eventBox
                this.eventBox.accept(((SingleTunnelSession<UID>) newSession).getEventBox());
            }
            if (newTunnel != oldTunnel)
                oldTunnel.close();  // 关闭旧 Tunnel
            return this.currentTunnel;
        }
    }

    private SessionEventsBox<UID> getEventBox() {
        return eventBox;
    }

    private SessionReceiveEvent<UID> receiveEvent(NetTunnel<UID> tunnel, Message<UID> message, RespondMessageFuture<UID> future) {
        return new SessionReceiveEvent<>(tunnel, message, SessionEventType.MESSAGE, future);
    }

    private SessionSendEvent<UID> sendEvent(NetTunnel<UID> tunnel, MessageContent<UID> content) {
        return new SessionSendEvent<>(tunnel, content);
    }

    @Override
    public String toString() {
        return "Session [" + this.getUserGroup() + "." + this.getUid() + " | " + this.currentTunnel + "]";
    }

}
