package com.tny.game.net.message;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.base.*;

/**
 * Rpc节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 05:10
 **/
@ProtobufClass
public class ForwardRpcServicer implements RpcServicer {

    @Protobuf(order = 1)
    private int serviceTypeId;

    private RpcServiceType serviceType;

    @Protobuf(order = 2)
    private RpcAccessId id;

    public ForwardRpcServicer() {
    }

    public ForwardRpcServicer(RpcServicer servicer) {
        this.serviceType = servicer.getServiceType();
        this.serviceTypeId = this.serviceType.id();
        this.id = new RpcAccessId(servicer.getId());
    }

    public ForwardRpcServicer(RpcServiceType serviceType) {
        this.serviceType = serviceType;
        this.serviceTypeId = serviceType.id();
    }

    public ForwardRpcServicer(RpcServiceType serviceType, int serverId, long id) {
        this.serviceType = serviceType;
        this.serviceTypeId = serviceType.id();
        this.id = new RpcAccessId(id);
    }

    @Override
    public RpcServiceType getServiceType() {
        if (serviceType == null) {
            return serviceType = RpcServiceTypes.of(serviceTypeId);
        }
        return serviceType;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public String getService() {
        return getServiceType().getService();
    }

    public boolean isHasAccessId() {
        return this.id != null;
    }

    @Override
    public int getServerId() {
        return id == null ? -1 : id.getServiceId();
    }

    @Override
    public long getId() {
        return id == null ? -1 : id.getId();
    }

}
