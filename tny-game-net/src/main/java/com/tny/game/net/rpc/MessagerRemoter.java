package com.tny.game.net.rpc;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Rpc 转发节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:15
 **/
public class MessagerRemoter implements RpcRemoterSet, RpcRemoterNode {

    private MessagerType messagerType;

    private EndpointKeeper<Object, Endpoint<Object>> keeper;

    private final List<MessagerRemoter> remoterList;

    public MessagerRemoter(MessagerType messagerType) {
        this.messagerType = messagerType;
        this.remoterList = Collections.singletonList(this);
    }

    void bind(EndpointKeeper<?, ? extends Endpoint<?>> keeper) {
        this.keeper = as(keeper);
    }

    @Override
    public List<? extends RpcRemoterNode> getOrderRemoterNodes() {
        return remoterList;
    }

    @Override
    public RpcRemoterNode findRemoterNode(int nodeId) {
        return this;
    }

    @Override
    public RpcRemoterAccess findRemoterAccess(int nodeId, long accessId) {
        return getRemoterAccess(accessId);
    }

    @Override
    public int getNodeId() {
        return 0;
    }

    @Override
    public RpcServiceType getServiceType() {
        return null;
    }

    @Override
    public List<? extends RpcRemoterAccess> getOrderRemoterAccesses() {
        return Collections.emptyList();
    }

    @Override
    public RpcRemoterAccess getRemoterAccess(long accessId) {
        Endpoint<?> endpoint = keeper.getEndpoint(accessId);
        if (endpoint != null) {
            return MessagerRemoterAccess.of(endpoint);
        }
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}
