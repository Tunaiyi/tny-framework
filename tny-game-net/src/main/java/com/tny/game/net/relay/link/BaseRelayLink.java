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
package com.tny.game.net.relay.link;

import com.tny.game.common.event.*;
import com.tny.game.common.notifier.*;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.listener.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.builder.*;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/19 4:39 下午
 */
public abstract class BaseRelayLink implements NetRelayLink {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private long latelyHeartbeatTime = System.currentTimeMillis();

    /**
     * 连接 id
     */
    private final String id;

    /**
     * 唯一 key
     */
    private final String key;

    /**
     * 集群id
     */
    private final String service;

    /**
     * 集群实例(节点) id
     */
    private final long instanceId;

    /**
     * 接入模式
     */
    private final NetAccessMode accessMode;

    /**
     * 创建时间
     */
    private final long createAt;

    /**
     * 转发链路状态
     */
    private volatile RelayLinkStatus status = RelayLinkStatus.INIT;

    /**
     * 转发发送器
     */
    protected final RelayTransport transport;

    /**
     * 转发关掉事件
     */
    private final EventNotifier<RelayLinkListener, NetRelayLink> event = EventNotifiers.notifier(RelayLinkListener.class);

    /**
     * 数据包 id 创建器
     */
    private final AtomicInteger packetIdCreator = new AtomicInteger();

    private final ContactType contactType;

    private final Lock statusLock = new ReentrantLock();


    public BaseRelayLink(NetAccessMode accessMode, String key, ContactType contactType, String service, long instanceId,
            RelayTransport transport) {
        this.key = key;
        this.service = service;
        this.instanceId = instanceId;
        this.accessMode = accessMode;
        this.id = NetRelayLink.idOf(this);
        this.transport = transport;
        this.createAt = System.currentTimeMillis();
        this.contactType = contactType;
        this.transport.bind(this);
    }

    @Override
    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public long getContactId() {
        return instanceId;
    }

    @Override
    public ContactType getContactType() {
        return contactType;
    }

    @Override
    public NetAccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public long getCreateTime() {
        return this.createAt;
    }

    @Override
    public RelayLinkStatus getStatus() {
        return this.status;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.transport.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.transport.getLocalAddress();
    }


    @Override
    public boolean isCurrentTransport(RelayTransport transport) {
        return Objects.equals(this.transport, transport);
    }

    @Override
    public boolean isActive() {
        return this.status == RelayLinkStatus.OPEN && this.isConnected();
    }

    private boolean isConnected() {
        RelayTransport transport = this.transport;
        return transport != null && transport.isActive();
    }

    @Override
    public void heartbeat() {
        this.latelyHeartbeatTime = System.currentTimeMillis();
    }

    @Override
    public long getLatelyHeartbeatTime() {
        return latelyHeartbeatTime;
    }

    @Override
    public MessageWriteFuture relay(RelayTunnel from, Message message, MessageWriteFuture awaiter) {
        return this.transport.write(new TunnelRelayPacket(createPacketId(), from.getInstanceId(), from.getId(), message), awaiter);
    }

    @Override
    public MessageWriteFuture relay(RelayTunnel from, MessageAllocator allocator, MessageFactory factory, MessageContent content) {
        return this.transport.write(
                () -> new TunnelRelayPacket(createPacketId(), from.getInstanceId(), from.getId(), allocator.allocate(factory, content)),
                content.getWriteFuture());
    }

    @Override
    public <P extends RelayPacket<A>, A extends RelayPacketArguments> MessageWriteFuture write(RelayPacketFactory<P, A> factory, A arguments,
            boolean promise) {
        return this.transport.write(factory.createPacket(createPacketId(), arguments, System.currentTimeMillis()),
                promise ? new MessageWriteFuture() : null);
    }

    @Override
    public void ping() {
        this.transport.write(LinkHeartBeatPacket.ping(createPacketId()), null);
    }

    @Override
    public void pong() {
        this.transport.write(LinkHeartBeatPacket.pong(createPacketId()), null);
    }

    @Override
    public EventWatch<RelayLinkListener> eventWatch() {
        return event;
    }

    @Override
    public void open() {
        if (this.status != RelayLinkStatus.INIT || !this.transport.isActive()) {
            return;
        }
        statusLock.lock();
        try {
            if (this.status != RelayLinkStatus.INIT || !this.transport.isActive()) {
                return;
            }
            this.status = RelayLinkStatus.OPEN;
            this.onOpen();
            this.heartbeat();
            event.notify(RelayLinkListener::onOpen, this);
        } finally {
            statusLock.unlock();
        }
    }

    protected void onOpen() {
    }

    public void openOnFailure() {
        if (this.status != RelayLinkStatus.INIT) {
            return;
        }
        statusLock.lock();
        try {
            if (this.status != RelayLinkStatus.INIT) {
                return;
            }
            this.write(LinkOpenedPacket.FACTORY, LinkOpenedArguments.failure(), true);
            this.doDisconnect();
            this.onOpenFailure();
        } finally {
            statusLock.unlock();
        }
    }

    protected void onOpenFailure() {
    }

    @Override
    public void disconnect() {
        if (this.status != RelayLinkStatus.OPEN) {
            return;
        }
        statusLock.lock();
        try {
            if (this.status != RelayLinkStatus.OPEN) {
                return;
            }
            this.status = RelayLinkStatus.DISCONNECT;
            this.doDisconnect();
        } finally {
            statusLock.unlock();
        }
    }

    protected void onDisconnect() {
    }

    private void doDisconnect() {
        RelayTransport transport = this.transport;
        if (transport != null && transport.isActive()) {
            transport.close();
        }
        event.notify(RelayLinkListener::onDisconnect, this);
        LOGGER.info("RelayLink [{}:{}] 转发链接断开", this, this.status);
        this.onDisconnect();
    }

    @Override
    public boolean close() {
        if (this.status.isCloseStatus()) {
            return false;
        }
        statusLock.lock();
        try {
            if (this.status.isCloseStatus()) {
                return false;
            }
            this.status = RelayLinkStatus.CLOSING;
            event.notify(RelayLinkListener::onClosing, this);
            this.write(LinkClosePacket.FACTORY, null);
            this.doDisconnect();
            this.status = RelayLinkStatus.CLOSED;
            event.notify(RelayLinkListener::onClosed, this);
            LOGGER.info("RelayLink [{}:{}] 转发链接关闭 ", this, this.status);
            this.onClosed();
            return true;
        } finally {
            statusLock.unlock();
        }
    }

    @Override
    public boolean isClosed() {
        return this.status.isCloseStatus();
    }

    protected void onClosed() {
    }

    @Override
    public void closeTunnel(RelayTunnel tunnel) {
        this.write(TunnelDisconnectPacket.FACTORY, new TunnelVoidArguments(tunnel));
    }

    private int createPacketId() {
        return this.packetIdCreator.incrementAndGet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseRelayLink that)) {
            return false;
        }
        return new EqualsBuilder().append(getInstanceId(), that.getInstanceId())
                .append(getId(), that.getId())
                .append(getService(), that.getService())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getService()).append(getInstanceId()).toHashCode();
    }

    @Override
    public String toString() {
        return "RelayLink(" + this.getAccessMode() + ")[" + this.getId() + "]" + this.transport;
    }

}
