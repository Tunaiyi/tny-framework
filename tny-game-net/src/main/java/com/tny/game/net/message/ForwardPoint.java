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
            this.accessId = new RpcAccessId(servicer.getMessagerId());
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
    public long getMessagerId() {
        return accessId == null ? -1 : accessId.getId();
    }

    @Override
    public int getServerId() {
        return accessId == null ? -1 : accessId.getServiceId();
    }

    public ForwardPoint setAccessId(RpcAccessId accessId) {
        this.accessId = accessId;
        return this;
    }

}
