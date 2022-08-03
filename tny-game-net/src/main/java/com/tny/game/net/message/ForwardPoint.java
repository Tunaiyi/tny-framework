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
public class ForwardPoint implements RpcServicerPoint {

    @Protobuf(order = 1)
    private int serviceTypeId;

    @Ignore
    private RpcServiceType serviceType;

    @Protobuf(order = 2)
    private RpcAccessId accessId;

    public ForwardPoint() {
    }

    public ForwardPoint(RpcServicer servicer) {
        this.serviceType = servicer.getServiceType();
        this.serviceTypeId = this.serviceType.id();
        if (servicer instanceof RpcServicerPoint) {
            this.accessId = new RpcAccessId(((RpcServicerPoint)servicer).getId());
        }
    }

    public ForwardPoint(RpcServiceType serviceType) {
        this.serviceType = serviceType;
        this.serviceTypeId = serviceType.id();
    }

    public ForwardPoint(RpcServiceType serviceType, long accessId) {
        this.serviceType = serviceType;
        this.serviceTypeId = serviceType.id();
        this.accessId = new RpcAccessId(accessId);
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

    public RpcAccessId getAccessId() {
        return accessId;
    }

    public boolean isAccurately() {
        return this.accessId != null;
    }

    @Override
    public int getServerId() {
        return accessId == null ? -1 : accessId.getServiceId();
    }

    public ForwardPoint setAccessId(RpcAccessId accessId) {
        this.accessId = accessId;
        return this;
    }

    @Override
    public long getId() {
        return accessId == null ? -1 : accessId.getId();
    }

}
