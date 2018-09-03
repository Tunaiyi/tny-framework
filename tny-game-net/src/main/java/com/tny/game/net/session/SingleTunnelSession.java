package com.tny.game.net.session;

import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.MessageEvent.SessionEventType;
import com.tny.game.net.tunnel.*;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tny.game.net.session.SessionEvents.*;
import static org.slf4j.LoggerFactory.*;

/**
 * 单个 Tunnel 的 Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class SingleTunnelSession<UID> extends AbstractSession<UID> implements NetSession<UID> {

    public static final Logger LOGGER = getLogger(SingleTunnelSession.class);

    private volatile long offlineTime;

    private volatile NetTunnel<UID> currentTunnel;

    private AtomicInteger sessionState = new AtomicInteger(SessionState.ONLINE.getId());

    @SuppressWarnings("unchecked")
    public SingleTunnelSession(NetTunnel<UID> tunnel, UID unloginUID, MessageInputEventHandler<UID, NetTunnel<UID>> inputEventHandler, MessageOutputEventHandler<UID, NetTunnel<UID>> outputEventHandler, int cacheMessageSize) {
        super(unloginUID, inputEventHandler, outputEventHandler, cacheMessageSize);
        this.currentTunnel = tunnel;
        this.currentTunnel.bind(this);
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
    public void acceptTunnel(NetTunnel<UID> tunnel) throws ValidatorFailException {
        if (this.getId() == tunnel.getId())
            return;
        Throws.checkNotNull(tunnel, "newSession is null");
        NetCertificate<UID> newCertificate = tunnel.getCertificate();
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
                NetTunnel<UID> current = doAcceptTunnel(newCertificate, tunnel);
                ON_ONLINE.notify(this, current);
                return;
            }
        }
    }

    NetTunnel<UID> doAcceptTunnel(NetCertificate<UID> newCertificate, NetTunnel<UID> newTunnel) {
        synchronized (this) {
            this.certificate = newCertificate;
            this.offlineTime = 0;
            NetTunnel<UID> oldTunnel = this.currentTunnel;
            this.currentTunnel = newTunnel;
            this.currentTunnel.bind(this); //新Tunnel绑定当前 session
            if (newTunnel != oldTunnel)
                oldTunnel.close();  // 关闭旧 Tunnel
            return this.currentTunnel;
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
                tunnel.addInputEvent(receiveEvent(thenNullGetCurrent(tunnel), message, future));
                inputEventHandler.onInput(tunnel);
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
            tunnel.addOutputEvent(sendEvent(tunnel, content));
            outputEventHandler.onOutput(tunnel);
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
            tunnel.addOutputEvent(new MessageResendEvent<>(thenNullGetCurrent(tunnel), message));
        } catch (Throwable e) {
            message.resendFailed(e);
            LOGGER.warn("resend exception: {} | message: {}", e.getClass(), e.getMessage());
        }
    }


    private NetTunnel<UID> thenNullGetCurrent(NetTunnel<UID> tunnel) {
        return tunnel == null ? this.currentTunnel : tunnel;
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
    public void write(MessageSendEvent<UID> event) throws TunnelWriteException {
        synchronized (this) {
            Optional<NetTunnel<UID>> tunnelOpt = event.getTunnel();
            if (tunnelOpt.isPresent()) {
                try {
                    NetTunnel<UID> tunnel = tunnelOpt.get();
                    Message<UID> message = tunnel.getMessageBuilderFactory().newBuilder()
                            .setID(createMessageID())
                            .setSessionID(this.getId())
                            .setContent(event.getContent())
                            .setTunnel(tunnel)
                            .build();
                    this.addSentMessage(message);
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


    private MessageReceiveEvent<UID> receiveEvent(NetTunnel<UID> tunnel, Message<UID> message, RespondMessageFuture<UID> future) {
        return new MessageReceiveEvent<>(tunnel, message, SessionEventType.MESSAGE, future);
    }

    private MessageSendEvent<UID> sendEvent(NetTunnel<UID> tunnel, MessageContent<UID> content) {
        return new MessageSendEvent<>(tunnel, content);
    }

    @Override
    public String toString() {
        return "Session [" + this.getUserGroup() + "." + this.getUid() + " | " + this.currentTunnel + "]";
    }

}
