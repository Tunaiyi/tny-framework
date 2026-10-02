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
package com.tny.game.net.message;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.net.application.*;

/**
 * Rpc节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 05:10
 **/
@ProtobufClass
public class ForwardPoint implements RpcAccessPoint {

    @Protobuf(order = 1)
    private int serviceTypeId;

    @Ignore
    private RpcServiceType serviceType;

    @Protobuf(order = 2)
    private RpcAccessId accessId;

    public ForwardPoint() {
    }

    public ForwardPoint(RpcServicer service) {
        this.serviceType = service.getServiceType();
        this.serviceTypeId = this.serviceType.id();
        if (service instanceof RpcAccessPoint) {
            var point = (RpcAccessPoint) service;
            this.accessId = new RpcAccessId(point.getContactId());
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

    public boolean isAppointed() {
        return this.accessId != null;
    }

    @Override
    public long getContactId() {
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
