/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.session;

import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.MessageCommandBox;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 */
public abstract class BaseNetSession extends BaseCommunicator implements NetSession {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseNetSession.class);

    /* 会话 ID */
    private final long id;

    /* 通讯管道 */
    protected volatile NetTunnel tunnel;

    /* 认证 */
    protected Certificate certificate;

    /* 状态 */
    private volatile SessionStatus state;

    /* ID 生成器 */
    private final AtomicLong idCreator = new AtomicLong(0);

    /* 消息盒子 */
    private final MessageCommandBox commandBox;

    /* 上下文 */
    private final SessionContext context;

    /* 写出的消息缓存 */
    private final MessageQueue sentMessageQueue;

    /* 响应 future 管理器 */
    private volatile RespondFutureMonitor respondFutureMonitor;

    private final SessionEventBuses buses = new SessionEventBuses();

    /* 离线时间 */
    private volatile long offlineTime;

    /* 接收消息过滤器 */
    private volatile MessageHandleFilter receiveFilter = MessageHandleFilter.allHandleFilter();

    /* 发送消息过滤器 */
    private volatile MessageHandleFilter sendFilter = MessageHandleFilter.allHandleFilter();

    protected BaseNetSession(Certificate certificate, SessionContext context, NetTunnel tunnel, int sendMessageCachedSize) {
        this.id = ConnectIdFactory.newSessionId();
        this.state = SessionStatus.INIT;
        this.context = context;
        this.certificate = certificate;
        var commandExecutorFactory = context.getCommandExecutorFactory();
        if (sendMessageCachedSize > 0) {
            this.sentMessageQueue = new MessageQueue(sendMessageCachedSize);
        } else {
            this.sentMessageQueue = new MessageQueue(0);
        }
        this.commandBox = new MessageCommandBox(commandExecutorFactory.create(this));
        this.updateTunnel(tunnel);
    }

    @Override
    public SessionEventWatches events() {
        return buses;
    }

    @Override
    public SessionContext getContext() {
        return this.context;
    }

    @Override
    public Certificate getCertificate() {
        return this.certificate;
    }

    @Override
    public NetAccessMode getAccessMode() {
        return tunnel.getAccessMode();
    }

    private RespondFutureMonitor respondFutureMonitor() {
        if (this.respondFutureMonitor != null) {
            return this.respondFutureMonitor;
        }
        synchronized (this) {
            if (this.respondFutureMonitor != null) {
                return this.respondFutureMonitor;
            }
            return this.respondFutureMonitor = RespondFutureMonitor.getHolder(this);
        }
    }

    private void putFuture(long messageId, MessageRespondFuture respondFuture) {
        if (respondFuture == null) {
            return;
        }
        respondFutureMonitor().putFuture(messageId, respondFuture);
    }

    private MessageRespondFuture pollFuture(Message message) {
        RespondFutureMonitor respondFutureHolder = this.respondFutureMonitor;
        if (respondFutureHolder == null) {
            return null;
        }
        if (message.getMode() == MessageMode.RESPONSE) {
            return respondFutureHolder.pollFuture(message.getToMessage());
        }
        return null;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setSendFilter(MessageHandleFilter filter) {
        if (filter == null) {
            filter = MessageHandleFilter.allHandleFilter();
        }
        this.sendFilter = filter;
    }

    @Override
    public void setReceiveFilter(MessageHandleFilter filter) {
        if (filter == null) {
            filter = MessageHandleFilter.allHandleFilter();
        }
        this.receiveFilter = filter;
    }

    @Override
    public boolean receive(RpcEnterContext rpcContext) {
        RpcRejectReceiveException cause;
        var result = MessageHandleStrategy.HANDLE;
        try {
            MessageHandleFilter filter = this.getReceiveFilter();
            var tunnel = rpcContext.netTunnel();
            var message = rpcContext.getMessage();
            MessageRespondFuture future = this.pollFuture(message);
            if (future != null) {
                this.commandBox.execute(new RespondFutureTask(rpcContext, future));
            }
            if (filter != null) {
                result = filter.filter(this, message);
            }
            if (result.isHandleable()) {
                return this.commandBox.addCommand(rpcContext);
            } else {
                cause = new RpcRejectReceiveException(rejectMessage(true, filter, message, tunnel));
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
            rpcContext.complete(e);
            throw new NetException(NetResultCode.SERVER_ERROR, e);
        }
        LOGGER.warn("", cause);
        rpcContext.complete(cause);
        if (result.isThrowable()) {
            throw cause;
        }
        return true;
    }

    private String rejectMessage(boolean receive, MessageHandleFilter filter, MessageSubject message, Tunnel tunnel) {
        return format("{} cannot {} {} from {} after being filtered by {}", this, receive ? "receive" : "send", message, tunnel, filter);
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        this.commandBox.execute(command);
    }

    @Override
    public MessageSent send(NetTunnel tunnel, MessageContent content) {
        RpcRejectSendException cause;
        var result = MessageHandleStrategy.HANDLE;
        if (this.isClosed()) {
            content.cancel(new SessionClosedException(format("session {} closed", this)));
            return content;
        }
        try {
            if (tunnel == null) {
                tunnel = tunnel();
            }
            MessageHandleFilter filter = this.getSendFilter();
            if (filter != null) {
                result = filter.filter(this, content);
            }
            if (result.isHandleable()) {
                tunnel.write(this::createMessage, content);
                return content;
            }
            cause = new RpcRejectSendException(rejectMessage(false, filter, content, tunnel));
        } catch (Throwable e) {
            LOGGER.error("", e);
            content.cancel(e);
            throw new NetException(NetResultCode.SERVER_ERROR, e);
        }
        LOGGER.warn("", cause);
        content.cancel(cause);
        if (result.isThrowable()) {
            throw cause;
        }
        return content;
    }

    @Override
    public void resend(NetTunnel tunnel, Predicate<Message> filter) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = tunnel();
        }
        for (Message message : this.getSentMessages(filter)) {
            tunnel.write(message, null);
        }
    }

    @Override
    public void resend(NetTunnel tunnel, long fromId, FilterBound bound) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = tunnel();
        }
        for (Message message : this.getSentMessages(fromId, bound)) {
            tunnel.write(message, null);
        }
    }

    @Override
    public void resend(NetTunnel tunnel, long fromId, long toId, FilterBound bound) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = tunnel();
        }
        for (Message message : this.getSentMessages(fromId, toId, bound)) {
            tunnel.write(message, null);
        }
    }

    //    @Override
    //    public void takeOver(MessageCommandBox commandTaskBox) {
    //        this.commandBox.takeOver(commandTaskBox);
    //    }

    @Override
    public MessageSent send(MessageContent content) {
        return this.send(null, content);
    }

    @Override
    public NetTunnel tunnel() {
        return this.tunnel;
    }

    @Override
    public MessageHandleFilter getSendFilter() {
        return as(this.sendFilter);
    }

    @Override
    public MessageHandleFilter getReceiveFilter() {
        return as(this.receiveFilter);
    }

    @Override
    public MessageCommandBox getCommandBox() {
        return this.commandBox;
    }

    @Override
    public NetMessage createMessage(MessageFactory messageFactory, MessageContent context) {
        NetMessage message = messageFactory.create(allocateMessageId(), context);
        if (context instanceof RequestContent) {
            this.putFuture(message.getId(), ((RequestContent) context).getRespondFuture());
        }
        this.sentMessageQueue.addMessage(message);
        return message;
    }

    private long allocateMessageId() {
        return this.idCreator.incrementAndGet();
    }

    @Override
    public List<Message> getSentMessages(Predicate<Message> filter) {
        return this.sentMessageQueue.getMessages(filter);
    }

    @Override
    public List<Message> getAllSendMessages() {
        return this.sentMessageQueue.getAllMessages();
    }

    @Override
    public SessionStatus getStatus() {
        return this.state;
    }

    @Override
    public long getOfflineTime() {
        return this.offlineTime;
    }

    private void setOnline() {
        this.offlineTime = 0;
        this.state = SessionStatus.ONLINE;
        buses.onlineEvent().notify(this);
    }

    protected void setOffline() {
        this.offlineTime = System.currentTimeMillis();
        this.state = SessionStatus.OFFLINE;
        buses.offlineEvent().notify(this);
    }

    private void setClose() {
        this.state = SessionStatus.CLOSE;
        this.destroyFutureHolder();
        buses.closeEvent().notify(this);
    }

    private void destroyFutureHolder() {
        RespondFutureMonitor.removeHolder(this);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        NetTunnel tunnel = this.tunnel;
        return tunnel == null ? null : tunnel.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        NetTunnel tunnel = this.tunnel;
        return tunnel == null ? null : tunnel.getLocalAddress();
    }

    @Override
    public boolean isActive() {
        NetTunnel tunnel = this.tunnel;
        return tunnel != null && tunnel.isActive();
    }

    @Override
    public boolean isOnline() {
        return this.state == SessionStatus.ONLINE;
    }

    @Override
    public boolean isOffline() {
        return this.state == SessionStatus.OFFLINE;
    }

    @Override
    public boolean isClosed() {
        return this.state == SessionStatus.CLOSE;
    }

    private void offlineIf(NetTunnel tunnel) {
        synchronized (this) {
            if (tunnel != tunnel()) {
                return;
            }
            if (!tunnel.isClosed()) {
                tunnel.close();
            }
            setOffline();
        }
    }

    @Override
    public void offline() {
        if (isClosed()) {
            return;
        }
        synchronized (this) {
            if (isClosed()) {
                return;
            }
            NetTunnel tunnel = tunnel();
            if (!tunnel.isClosed()) {
                tunnel.close();
            }
            setOffline();
        }
    }

    @Override
    public void heartbeat() {
        NetTunnel tunnel = tunnel();
        if (tunnel.isOpen()) {
            tunnel.ping();
        }
    }

    @Override
    public boolean closeWhen(SessionStatus status) {
        if (this.state != status) {
            return false;
        }
        synchronized (this) {
            if (this.state != status) {
                return false;
            }
            return this.close();
        }
    }

    @Override
    public boolean close() {
        if (this.state == SessionStatus.CLOSE) {
            return false;
        }
        synchronized (this) {
            if (this.state == SessionStatus.CLOSE) {
                return false;
            }
            this.offline();
            this.prepareClose();
            this.setClose();
            this.postClose();
            return true;
        }
    }

    protected void prepareClose() {
    }

    protected void postClose() {
    }

    private void checkOnlineCertificate(Certificate certificate) throws AuthFailedException {
        Certificate currentCert = this.certificate;
        if (!certificate.isAuthenticated()) {
            throw new AuthFailedException(NetResultCode.NO_LOGIN);
        }
        if (currentCert != null && currentCert.isAuthenticated() && !currentCert.isSameCertificate(certificate)) { // 是否是同一个授权
            throw new AuthFailedException("Certificate new [{}] 与 old [{}] 不同", certificate, this.certificate);
        }
        if (this.isClosed()) // 判断 session 状态是否可以重登
        {
            throw new AuthFailedException(NetResultCode.SESSION_LOSS_ERROR);
        }
    }

    @Override
    public void setSendMessageCachedSize(int messageSize) {
        if (this.sentMessageQueue != null) {
            this.sentMessageQueue.resize(messageSize);
        }
    }

    @Override
    public void online(Certificate certificate) throws AuthFailedException {
        checkOnlineCertificate(certificate);
        synchronized (this) {
            checkOnlineCertificate(certificate);
            this.certificate = certificate;
            this.setOnline();
        }
    }

    @Override
    public void online(Certificate certificate, NetTunnel tunnel) throws AuthFailedException {
        Asserts.checkNotNull(tunnel, "newSession is null");
        checkOnlineCertificate(certificate);
        synchronized (this) {
            checkOnlineCertificate(certificate);
            this.certificate = certificate;
            this.acceptTunnel(tunnel);
        }
    }

    private boolean updateTunnel(NetTunnel tunnel) {
        if (tunnel.bind(this)) {
            NetTunnel oldTunnel = this.tunnel;
            this.tunnel = tunnel;
            this.offlineTime = 0;
            if (oldTunnel != null && tunnel != oldTunnel) {
                oldTunnel.close();  // 关闭旧 Tunnel
            }
            return true;
        }
        return false;
    }

    // 接受 Tunnel
    private void acceptTunnel(NetTunnel newTunnel) throws AuthFailedException {
        if (updateTunnel(newTunnel)) {
            this.setOnline();
        } else {
            this.offlineIf(newTunnel);
            throw new AuthFailedException("{} tunnel is bound session", newTunnel);
        }
    }

}
