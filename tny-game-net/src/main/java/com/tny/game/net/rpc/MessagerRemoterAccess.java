package com.tny.game.net.rpc;

import com.tny.game.common.context.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

/**
 * Rpc 转发节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:15
 **/
public class MessagerRemoterAccess implements RpcRemoterAccess {

    private static final AttrKey<MessagerRemoterAccess> REMOTER_ACCESS = AttrKeys.key(MessagerRemoterAccess.class, "REMOTER_ACCESS");

    private final Endpoint<?> endpoint;

    public static MessagerRemoterAccess of(Endpoint<?> endpoint) {
        MessagerRemoterAccess access = endpoint.attributes().getAttribute(REMOTER_ACCESS);
        if (access != null) {
            return access;
        }
        return endpoint.attributes().computeIfAbsent(REMOTER_ACCESS, () -> new MessagerRemoterAccess(endpoint));
    }

    private MessagerRemoterAccess(Endpoint<?> endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public boolean isActive() {
        return endpoint.isActive();
    }

    @Override
    public long getAccessId() {
        return endpoint.getMessagerId();
    }

    @Override
    public SendReceipt send(MessageContext messageContext) {
        return endpoint.send(messageContext);
    }

}
