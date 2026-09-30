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
package com.tny.game.net.transport;

import com.tny.game.net.application.NetBootstrapContext;
import com.tny.game.net.message.Message;
import com.tny.game.net.rpc.NetAccessMode;
import com.tny.game.net.session.MessageAllocator;
import com.tny.game.net.session.NetSession;

import java.net.InetSocketAddress;

/**
 * 测试支撑：session/transport 各测试文件内联同构隧道桩（TestTunnel/RacingTunnel 等变体）的下沉夹具，
 * 形态照同模块 test 源集先例 RpcContextFixture（同包落位、公共 test 包不另设）。
 * <p>
 * 钩子默认行为与下沉前各文件逐字一致：resetSession 直写 session 字段并返回 true、onOpen 恒 true、
 * onOpened/onClose/onClosed/onDisconnected/doDisconnect 空实现、双地址桩按构造参数固定端口、
 * write 最简透传（allocator 形参恒 null、promise 形参原样返回）。
 * 个性化写出/竞态钩子（RacingTunnel、CapturingTunnel、计数与广播捕获等）由子类覆写 write 承载，
 * close/disconnect 走 BaseNetTunnel 真实路径的语义不变。仅供 tny-game-net test 源集使用。
 */
public class TestTunnelFixture extends BaseNetTunnel<NetSession> {

    /**
     * isActive 桩的三种现状形态（下沉前逐字保留，不得归并）：
     * STATUS_OPEN 等价 getStatus() == TunnelStatus.OPEN；NOT_CLOSED 等价 !isClosed()；
     * NEVER 恒 false（BroadcastIntent 借此让 open() 走完整激活流程置位 OPEN，write 门控用 isOpen）。
     */
    public enum ActivePolicy {
        STATUS_OPEN,
        NOT_CLOSED,
        NEVER
    }

    private final ActivePolicy activePolicy;
    private final int remotePort;
    private final int localPort;

    public TestTunnelFixture(ActivePolicy activePolicy, int remotePort, int localPort) {
        super(1L, NetAccessMode.SERVER, new NetBootstrapContext());
        this.activePolicy = activePolicy;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }

    @Override
    protected boolean resetSession(NetSession newSession) {
        this.session = newSession;
        return true;
    }

    @Override
    protected boolean onOpen() {
        return true;
    }

    @Override
    protected void onOpened() {
    }

    @Override
    protected void onClose() {
    }

    @Override
    protected void onClosed() {
    }

    @Override
    protected void onDisconnected() {
    }

    @Override
    protected void doDisconnect() {
    }

    @Override
    public boolean isActive() {
        return switch (activePolicy) {
            case STATUS_OPEN -> getStatus() == TunnelStatus.OPEN;
            case NOT_CLOSED -> !isClosed();
            case NEVER -> false;
        };
    }

    @Override
    public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) {
        return null;
    }

    @Override
    public MessageWriteFuture write(Message message, MessageWriteFuture promise) {
        return promise;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(remotePort);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(localPort);
    }

}
