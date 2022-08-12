/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.endpoint;

import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.task.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.endpoint.EndpointEventBuses.*;

/**
 * <p>
 */
public abstract class BaseNetEndpoint<UID> extends AbstractCommunicator<UID> implements NetEndpoint<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseNetEndpoint.class);

    /* 终端 ID */
    private final long id;

    /* 通讯管道 */
    protected volatile NetTunnel<UID> tunnel;

    /* 认证 */
    protected Certificate<UID> certificate;

    /* 状态 */
    private volatile EndpointStatus state;

    /* ID 生成器 */
    private final AtomicLong idCreator = new AtomicLong(0);

    /* 消息盒子 */
    private final CommandTaskBox commandTaskBox;

    /**
     * 上下文
     */
    private final EndpointContext context;

    /* 写出的消息缓存 */
    private final MessageQueue<UID> sentMessageQueue;

    /* 响应 future 管理器 */
    private volatile RespondFutureMonitor respondFutureMonitor;

    /* 离线时间 */
    private volatile long offlineTime;

    /* 接收消息过滤器 */
    private volatile MessageHandleFilter<UID> receiveFilter = MessageHandleFilter.allHandleFilter();

    /* 发送消息过滤器 */
    private volatile MessageHandleFilter<UID> sendFilter = MessageHandleFilter.allHandleFilter();

    protected BaseNetEndpoint(SessionSetting setting, Certificate<UID> certificate, EndpointContext context) {
        this.id = NetAide.newEndpointId();
        this.state = EndpointStatus.INIT;
        this.context = context;
        this.certificate = certificate;
        this.commandTaskBox = new CommandTaskBox(context.getCommandTaskProcessor());
        if (setting != null) {
            this.sentMessageQueue = new MessageQueue<>(setting.getSendMessageCachedSize());
        } else {
            this.sentMessageQueue = new MessageQueue<>(0);
        }
    }

    @Override
    public EndpointContext getContext() {
        return this.context;
    }

    @Override
    public Certificate<UID> getCertificate() {
        return this.certificate;
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

    private void putFuture(long messageId, MessageRespondAwaiter respondFuture) {
        if (respondFuture == null) {
            return;
        }
        respondFutureMonitor().putFuture(messageId, respondFuture);
    }

    private <M> MessageRespondAwaiter pollFuture(Message message) {
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
    public void setSendFilter(MessageHandleFilter<UID> filter) {
        if (filter == null) {
            filter = MessageHandleFilter.allHandleFilter();
        }
        this.sendFilter = filter;
    }

    @Override
    public void setReceiveFilter(MessageHandleFilter<UID> filter) {
        if (filter == null) {
            filter = MessageHandleFilter.allHandleFilter();
        }
        this.receiveFilter = filter;
    }

    @Override
    public boolean receive(NetTunnel<UID> tunnel, Message message) {
        MessageHandleFilter<UID> filter = this.getReceiveFilter();
        if (filter != null) {
            boolean throwable = true;
            switch (filter.filter(this, message)) {
                case IGNORE:
                    throwable = false;
                case THROW:
                    if (throwable) {
                        String causeMessage = format("{} cannot receive {} from {} after being filtered by {}",
                                this, message, tunnel, filter.getClass());
                        LOGGER.warn(causeMessage);
                        throw new EndpointException(causeMessage);
                    }
                    return true;
                default:
                    break;
            }
        }
        MessageRespondAwaiter awaiter = this.pollFuture(message);
        if (awaiter != null) {
            this.commandTaskBox.addTask(new RespondCommandTask(message, awaiter));
        }
        return this.commandTaskBox.addTask(new MessageCommandTask(tunnel, message));
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        this.commandTaskBox.execute(command);
    }

    @Override
    public SendReceipt send(NetTunnel<UID> tunnel, MessageContext context) {
        try {
            if (this.isClosed()) {
                context.cancel(new EndpointCloseException(format("endpoint {} closed", this)));
                return context;
            }
            if (tunnel == null) {
                tunnel = currentTunnel();
            }
            MessageHandleFilter<UID> filter = this.getSendFilter();
            if (filter != null) {
                boolean throwable = true;
                switch (filter.filter(this, context)) {
                    case IGNORE:
                        throwable = false;
                    case THROW:
                        context.cancel(true);
                        String causeMessage = format("{} cannot send {} to {} after being filtered by {}", this, context, tunnel, filter.getClass());
                        LOGGER.warn(causeMessage);
                        if (throwable) {
                            throw new EndpointException(causeMessage);
                        }
                        return context;
                    case HANDLE:
                        break;
                }
            }
            tunnel.write(this::createMessage, context);
            return context;
        } catch (Exception e) {
            LOGGER.error("", e);
            context.cancel(e);
            throw new NetException(e);
        }
    }

    @Override
    public boolean receive(Message message) {
        return receive(null, message);
    }

    @Override
    public void resend(NetTunnel<UID> tunnel, Predicate<Message> filter) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = currentTunnel();
        }
        //        this.eventsBox.addOutputEvent(new EndpointResendEvent<>(tunnel, filter));
        //        this.eventHandler.onOutput(this.eventsBox, this);
        for (Message message : this.getSentMessages(filter)) {
            tunnel.write(message, null);
        }
    }

    @Override
    public void resend(NetTunnel<UID> tunnel, long fromId, FilterBound bound) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = currentTunnel();
        }
        for (Message message : this.getSentMessages(fromId, bound)) {
            tunnel.write(message, null);
        }
    }

    @Override
    public void resend(NetTunnel<UID> tunnel, long fromId, long toId, FilterBound bound) {
        if (this.isClosed()) {
            return;
        }
        if (tunnel == null) {
            tunnel = currentTunnel();
        }
        for (Message message : this.getSentMessages(fromId, toId, bound)) {
            tunnel.write(message, null);
        }
    }

    @Override
    public void takeOver(CommandTaskBox commandTaskBox) {
        this.commandTaskBox.takeOver(commandTaskBox);
    }

    @Override
    public SendReceipt send(MessageContext messageContext) {
        return this.send(null, messageContext);
    }

    protected NetTunnel<UID> currentTunnel() {
        return this.tunnel;
    }

    @Override
    public MessageHandleFilter<UID> getSendFilter() {
        return as(this.sendFilter);
    }

    @Override
    public MessageHandleFilter<UID> getReceiveFilter() {
        return as(this.receiveFilter);
    }

    @Override
    public CommandTaskBox getCommandTaskBox() {
        return this.commandTaskBox;
    }

    //	@Override
    //	public void writeMessage(NetTunnel<UID> tunnel, MessageContext context) {
    //		MessageFactory messageFactory = this.tunnel.getMessageFactory();
    //		Message message = messageFactory.create(allocateMessageId(), context);
    //		RespondFuture respondFuture = context.getRespondFuture();
    //		if (respondFuture != null) {
    //			context.willWriteFuture((future) -> {
    //				if (future.isSuccess()) {
    //					this.putFuture(message.getId(), respondFuture);
    //				} else {
    //					respondFuture.completeExceptionally(future.cause());
    //				}
    //			});
    //		}
    //		this.tryCreateFuture(context);
    //		this.tunnel.write(message, as(context.getMessageWriteAwaiter()));
    //	}

    @Override
    public NetMessage createMessage(MessageFactory messageFactory, MessageContext context) {
        NetMessage message = messageFactory.create(allocateMessageId(), context);
        if (context instanceof RequestContext) {
            this.putFuture(message.getId(), ((RequestContext)context).getResponseAwaiter());
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
    public EndpointStatus getStatus() {
        return this.state;
    }

    @Override
    public long getOfflineTime() {
        return this.offlineTime;
    }

    private void setOnline() {
        this.offlineTime = 0;
        this.state = EndpointStatus.ONLINE;
        buses().onlineEvent().notify(this);
    }

    protected void setOffline() {
        this.offlineTime = System.currentTimeMillis();
        this.state = EndpointStatus.OFFLINE;
        buses().offlineEvent().notify(this);
    }

    private void setClose() {
        this.state = EndpointStatus.CLOSE;
        this.destroyFutureHolder();
        buses().closeEvent().notify(this);
    }

    private void destroyFutureHolder() {
        RespondFutureMonitor.removeHolder(this);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        NetTunnel<UID> tunnel = this.tunnel;
        return tunnel == null ? null : tunnel.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        NetTunnel<UID> tunnel = this.tunnel;
        return tunnel == null ? null : tunnel.getLocalAddress();
    }

    @Override
    public boolean isActive() {
        NetTunnel<UID> tunnel = this.tunnel;
        return tunnel != null && tunnel.isActive();
    }

    @Override
    public boolean isOnline() {
        return this.state == EndpointStatus.ONLINE;
    }

    @Override
    public boolean isOffline() {
        return this.state == EndpointStatus.OFFLINE;
    }

    @Override
    public boolean isClosed() {
        return this.state == EndpointStatus.CLOSE;
    }

    private void offlineIf(NetTunnel<UID> tunnel) {
        synchronized (this) {
            if (tunnel != currentTunnel()) {
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
            NetTunnel<UID> tunnel = currentTunnel();
            if (!tunnel.isClosed()) {
                tunnel.close();
            }
            setOffline();
        }
    }

    @Override
    public void heartbeat() {
        NetTunnel<UID> tunnel = currentTunnel();
        if (tunnel.isOpen()) {
            tunnel.ping();
        }
    }

    @Override
    public boolean closeWhen(EndpointStatus status) {
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
        if (this.state == EndpointStatus.CLOSE) {
            return false;
        }
        synchronized (this) {
            if (this.state == EndpointStatus.CLOSE) {
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

    private void checkOnlineCertificate(Certificate<UID> certificate) {
        Certificate<UID> currentCert = this.certificate;
        if (!certificate.isAuthenticated()) {
            throw new ValidatorFailException(NetResultCode.NO_LOGIN);
        }
        if (currentCert != null && currentCert.isAuthenticated() && !currentCert.isSameCertificate(certificate)) { // 是否是同一个授权
            throw new ValidatorFailException(format("Certificate new [{}] 与 old [{}] 不同", certificate, this.certificate));
        }
        if (this.isClosed()) // 判断 session 状态是否可以重登
        {
            throw new ValidatorFailException(NetResultCode.SESSION_LOSS_ERROR);
        }
    }

    @Override
    public void online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws ValidatorFailException {
        Asserts.checkNotNull(tunnel, "newSession is null");
        checkOnlineCertificate(certificate);
        synchronized (this) {
            checkOnlineCertificate(certificate);
            this.certificate = certificate;
            this.acceptTunnel(tunnel);
        }
    }

    // 接受 Tunnel
    private void acceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException {
        if (newTunnel.bind(this)) {
            NetTunnel<UID> oldTunnel = this.tunnel;
            this.tunnel = newTunnel;
            this.offlineTime = 0;
            if (oldTunnel != null && newTunnel != oldTunnel) {
                oldTunnel.close();  // 关闭旧 Tunnel
            }
            this.setOnline();
        } else {
            this.offlineIf(newTunnel);
            throw new ValidatorFailException(format("{} tunnel is bound session", newTunnel));
        }
    }

}
