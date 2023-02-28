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

import com.tny.game.common.concurrent.worker.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/21 17:41
 **/
public class LocalMessageTransporter implements MessageTransporter {

    public static final Logger LOGGER = LoggerFactory.getLogger(LocalMessageTransporter.class);

    private final AtomicBoolean closed = new AtomicBoolean();

    private final InetSocketAddress address;

    private final Executor executor;

    private NetTunnel<?> tunnel;

    public LocalMessageTransporter(String ip, int port, Executor executor) {
        this.address = new InetSocketAddress(ip, port);
        this.executor = AsyncWorker.createSerialWorker(executor);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return address;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return address;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public boolean close() {
        return closed.compareAndSet(false, true);
    }

    @Override
    public NetAccessMode getAccessMode() {
        return NetAccessMode.SERVER;
    }

    @Override
    public void bind(NetTunnel<?> tunnel) {
        this.tunnel = tunnel;
    }

    @Override
    public MessageWriteFuture write(Message message, MessageWriteFuture awaiter) throws NetException {
        if (this.tunnel != null) {
            var context = RpcTransactionContext.createEnter(this.tunnel, as(message), true);
            this.tunnel.receive(context);
            if (awaiter != null) {
                awaiter.complete(null);
            }
        } else {
            if (awaiter != null) {
                awaiter.completeExceptionally(new TunnelDisconnectedException("local tunnel is null"));
            }
        }
        return awaiter;
    }

    @Override
    public MessageWriteFuture write(MessageAllocator maker, MessageFactory factory, MessageContent context) throws NetException {
        MessageWriteFuture awaiter = context.getWriteFuture();
        executor.execute(() -> {
            Message message = null;
            try {
                message = maker.allocate(factory, context);
            } catch (Throwable e) {
                LOGGER.error("", e);
                if (awaiter != null) {
                    awaiter.completeExceptionally(e);
                }
            }
            if (message != null) {
                write(message, awaiter);
            }
        });
        return awaiter;
    }

}
