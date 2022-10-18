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
package com.tny.game.net.base;

import com.tny.game.common.utils.*;
import com.tny.game.net.message.*;

import java.util.Objects;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 15:11
 **/
public class RpcAccessIdentify implements RpcServicerPoint, Messager {

    private static final long RPC_MAX_INDEX = 0xFF;

    private static final long RPC_MAX_INDEX_MASK = 0xFF;

    private static final long RPC_SERVER_ID_INDEX = 0x7FFFFFFF;

    private static final int RPC_SERVER_ID_INDEX_SHIFT_SIZE = 8;

    private static final long RPC_SERVER_ID_INDEX_MASK = RPC_SERVER_ID_INDEX << RPC_SERVER_ID_INDEX_SHIFT_SIZE;

    private static final long RPC_MAX_SERVER_TYPE = 0x7FFFF;

    private static final int RPC_SERVICE_TYPE_MASK_SIZE = RPC_SERVER_ID_INDEX_SHIFT_SIZE + 32;

    private static final long RPC_SERVICE_TYPE_MASK = RPC_MAX_SERVER_TYPE << RPC_SERVICE_TYPE_MASK_SIZE;

    private long id;

    private transient RpcServiceType serviceType;

    private transient int serverId;

    public RpcAccessIdentify() {
    }

    //    public RpcAccessIdentify(String service, int serverId, int index) {
    //        checkIndex(index);
    //        this.id = formatId(RpcServiceTypes.checkService(service), serverId, index);
    //        this.serviceType = RpcServiceTypes.checkService(service);
    //        this.serverId = serverId;
    //    }

    public RpcAccessIdentify(RpcServiceType serviceType, int serverId, int index) {
        checkIndex(index);
        this.serviceType = serviceType;
        this.serverId = serverId;
        this.id = formatId(serviceType, serverId, index);
    }

    public RpcAccessIdentify(long id) {
        this.serviceType = parseServiceType(id);
        this.serverId = parseServerId(id);
        this.id = id;
    }

    public static RpcAccessIdentify parse(long id) {
        return new RpcAccessIdentify(id);
    }

    public static long formatId(RpcServiceType serviceType, int serverId, int index) {
        return ((long)serviceType.id() << RPC_SERVICE_TYPE_MASK_SIZE) | ((long)serverId << RPC_SERVER_ID_INDEX_SHIFT_SIZE) | index;
    }

    private static int parseIndex(long id) {
        return (int)(id & RPC_MAX_INDEX_MASK);
    }

    public static int parseServerId(long id) {
        return (int)((id & RPC_SERVER_ID_INDEX_MASK) >> RPC_SERVER_ID_INDEX_SHIFT_SIZE);
    }

    private static RpcServiceType parseServiceType(long id) {
        return RpcServiceTypes.of((int)((id & RPC_SERVICE_TYPE_MASK) >>> RPC_SERVICE_TYPE_MASK_SIZE));
    }

    private void checkIndex(int index) {
        Asserts.checkArgument(index <= RPC_MAX_INDEX, "index {} 必须 <= {}", index, RPC_MAX_INDEX);
    }

    @Override
    public RpcServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public long getMessagerId() {
        return serverId;
    }

    @Override
    public MessagerType getMessagerType() {
        return serviceType;
    }

    @Override
    public int getServerId() {
        return serverId;
    }

    @Override
    public long getId() {
        return id;
    }

    public int getIndex() {
        return parseIndex(id);
    }

    protected RpcAccessIdentify setServiceType(RpcServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    protected RpcAccessIdentify setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    protected RpcAccessIdentify setId(long id) {
        this.id = id;
        this.serviceType = parseServiceType(id);
        this.serverId = parseServerId(id);
        return this;
    }

    @Override
    public String toString() {
        String sb = "RpcAccessIdentify{" + "id=" + id +
                ", serviceType=" + serviceType +
                ", serverId=" + serverId +
                ", index=" + parseIndex(id) +
                '}';
        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RpcAccessIdentify)) {
            return false;
        }
        RpcAccessIdentify identify = (RpcAccessIdentify)o;
        return getId() == identify.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
