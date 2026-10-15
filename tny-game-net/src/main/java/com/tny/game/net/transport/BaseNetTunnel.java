/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.transport;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import org.slf4j.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 抽象通道
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class BaseNetTunnel<S extends NetSession> extends BaseCommunicator implements NetTunnel {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseNetTunnel.class);

    /*管道 id*/
    private final long id;

    /*访问 id*/
    private volatile long accessId;

    /* 管道模式 */
    private final NetAccessMode accessMode;

    /* 会话会话 */
    protected volatile S session;

    /* 上下文 */
    private final NetworkContext context;

    private final TunnelEvents buses = new TunnelEvents();

    // 失活通知（会话回调 + 事件）跨 disconnect/close/嵌套路径全隧道至多一次（net-tunnel"事件全序且单次"契约）
    private final AtomicBoolean unactivatedNotified = new AtomicBoolean(false);

    private volatile TunnelStatus status = TunnelStatus.INIT;
    
    private final Lock statusLock = new ReentrantLock();

    protected BaseNetTunnel(long id, NetAccessMode accessMode, NetworkContext context) {
        this.id = id;
        this.accessMode = accessMode;
        this.context = context;
    }

    protected BaseNetTunnel(long id, NetAccessMode accessMode, NetworkContext context, NetSession session) {
        this.id = id;
        this.accessMode = accessMode;
        this.context = context;
        this.bind(session);
    }

    @Override
    public TunnelEventWatches events() {
        return buses;
    }

    @Override
    public long getAccessId() {
        return this.accessId;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public NetAccessMode getAccessMode() {
        return this.accessMode;
    }

    @Override
    public boolean isClosed() {
        return this.status == TunnelStatus.CLOSED;
    }

    @Override
    public boolean isOpen() {
        return this.status == TunnelStatus.OPEN;
    }

    @Override
    public TunnelStatus getStatus() {
        return this.status;
    }

    @Override
    public Certificate getCertificate() {
        return this.session.getCertificate();
    }

    @Override
    public void setAccessId(long accessId) {
        this.accessId = accessId;
    }

    @Override
    public void ping() {
        this.write(TickMessage.ping(), null);
    }

    @Override
    public void pong() {
        this.write(TickMessage.pong(), null);
    }

    @Override
    public NetSession getSession() {
        return this.session;
    }

    protected void setSession(S session) {
        this.session = session;
    }

    @Override
    public NetworkContext getContext() {
        return this.context;
    }

    @Override
    public boolean receive(NetMessage message) {
        // 会话一致性由 doReceive 内对 volatile session 字段的单次快照读保证；
        // 不使用乐观读锁包装（业务有副作用，见 net-tunnel 规格与 design D1）
        return doReceive(message);
    }

    private boolean doReceive(NetMessage message) {
        S session = this.session;
        if (session == null) { // 未绑定显式拒绝（net-tunnel 规格）：先于上下文创建，拒绝不计入统计
            LOGGER.warn("[Tunnel] 通道 {} 会话未绑定，拒绝消息 id {} protocol {}",
                    getId(), message.getId(), message.getProtocolId());
            return false;
        }
        var rpcContext = RpcTransactionContext.createEnter(this, message, true);
        var rpcMonitor = this.context.getRpcMonitor();
        rpcMonitor.onReceive(rpcContext);
        if (session.isClosed()) {
            return false;
        }
        // 线性单路径：receive 恒返回 true 或抛异常（全仓实现核实），原 while(true) 重试枝不可达且诱发误读
        return session.receive(rpcContext);
    }

    @Override
    public MessageSent send(MessageContent content) {
        return doSend(content);
    }

    private MessageSent doSend(MessageContent messageContext) {
        return this.session.send(this, messageContext);
    }

    @Override
    public boolean bind(NetSession session) {
        if (session == null) {
            return false;
        }
        if (this.session == session) {
            return true;
        }
        statusLock.lock();
        try {
            if (this.session == session) {
                return true;
            }
            if (this.session == null) {
                this.session = as(session);
                return true;
            } else {
                Certificate certificate = session.getCertificate();
                if (!certificate.isAuthenticated()) {
                    return false;
                }
                // 会话切换互斥由本方法的 statusLock 提供；bind 是 resetSession 唯一切换入口，
                // 未来多步实现须经本入口进入以保持互斥（net-tunnel 规格 R1 约束）
                boolean switched = resetSession(session);
                if (switched) {
                    // 换绑新会话：失活通知资格对新会话重新开放
                    this.unactivatedNotified.set(false);
                }
                return switched;
            }
        } finally {
            statusLock.unlock();
        }
    }

    protected abstract boolean resetSession(NetSession session);

    @Override
    public boolean open() {
        if (this.isClosed()) {
            return false;
        }
        if (this.isActive()) {
            return true;
        }
        statusLock.lock();
        try {
            if (this.isClosed()) {
                return false;
            }
            if (this.isActive()) {
                return true;
            }
            if (!this.onOpen()) {
                return false;
            }
            this.status = TunnelStatus.OPEN;
            this.onOpened();
        } finally {
            statusLock.unlock();
        }
        buses.activateEvent().notify(this);
        return true;
    }

    @Override
    public void disconnect() {
        NetSession session;
        statusLock.lock();
        try {
            if (this.status == TunnelStatus.CLOSED || this.status == TunnelStatus.SUSPEND) {
                return;
            }
            this.doDisconnect();
            this.status = TunnelStatus.SUSPEND;
            session = this.session;
        } finally {
            statusLock.unlock();
        }
        // onDisconnected 与 close() 同为"锁内改状态、锁外回调"编排：
        // 客户端实现会在其中触达 close/session——持锁嵌套即成隧道锁×会话锁 ABBA（net-tunnel 契约）
        this.onDisconnected();
        notifyUnactivated(session);
    }

    @Override
    public boolean close() {
        if (this.status == TunnelStatus.CLOSED) {
            return false;
        }
        NetSession session;
        statusLock.lock();
        try {
            if (this.status == TunnelStatus.CLOSED) {
                return false;
            }
            this.status = TunnelStatus.CLOSED;
            this.onClose();
            this.doDisconnect();
            session = this.session;
            this.onClosed();
        } finally {
            statusLock.unlock();
        }
        // 关闭前保证失活先行（事件全序）；断开链路已通知时此处幂等跳过
        notifyUnactivated(session);
        buses.closeEvent().notify(this);
        return true;
    }

    private void notifyUnactivated(NetSession session) {
        if (!this.unactivatedNotified.compareAndSet(false, true)) {
            return;
        }
        if (session != null) { // 避免死锁
            session.onUnactivated(this);
        }
        buses.unactivatedEvent().notify(this);
    }

    @Override
    public void reset() {
        if (this.status == TunnelStatus.INIT) {
            return;
        }
        statusLock.lock();
        try {
            if (this.status == TunnelStatus.INIT) {
                return;
            }
            if (!this.isActive()) {
                this.disconnect();
            }
            this.status = TunnelStatus.INIT;
        } finally {
            statusLock.unlock();
        }
    }

    protected abstract void doDisconnect();

    protected abstract boolean onOpen();

    protected abstract void onOpened();

    protected abstract void onClose();

    protected abstract void onClosed();

    protected abstract void onDisconnected();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseNetTunnel<?> that)) {
            return false;
        }
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

}
