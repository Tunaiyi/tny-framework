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
package com.tny.game.net.application;

import com.tny.game.common.utils.*;

import java.util.Objects;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 15:11
 **/
public class RpcAccessIdentify implements RpcAccessPoint {

    private static final long RPC_SERVER_INDEX_SIZE = 10000;

    private static final long RPC_SERVICE_ID_SIZE = 100000000000L;

    private static final long RPC_SERVICE_TYPE_SIZE = RPC_SERVER_INDEX_SIZE * RPC_SERVICE_ID_SIZE;

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
        this.serviceType = checkParsedServiceType(id);
        this.serverId = parseServerId(id);
        this.id = id;
        checkRoundTrip(id);
    }

    public static RpcAccessIdentify parse(long id) {
        return new RpcAccessIdentify(id);
    }

    public static long formatId(RpcServiceType serviceType, int serverId, int index) {
        checkIndex(index);
        checkServerId(serverId);
        return ((long) serviceType.id() * RPC_SERVICE_TYPE_SIZE) + ((long) serverId * RPC_SERVER_INDEX_SIZE) + index;
    }

    private static int parseIndex(long id) {
        return (int) (id % RPC_SERVER_INDEX_SIZE);
    }

    public static int parseServerId(long id) {
        return (int) (id % RPC_SERVICE_TYPE_SIZE / RPC_SERVER_INDEX_SIZE);
    }

    private static RpcServiceType parseServiceType(long id) {
        return RpcServiceTypes.of((int) (id / RPC_SERVICE_TYPE_SIZE));
    }

    /**
     * 严格解析：未注册的服务类型数字（灰度新版本/异环境串网）必须以可诊断异常拒绝，
     * 不得静默构造出 serviceType==null 的半成品身份（后续 getContactType/NPE 或错路由）。
     */
    private static RpcServiceType checkParsedServiceType(long id) {
        Asserts.checkArgument(id >= 0, "identify id {} 不得为负", id);
        RpcServiceType serviceType = parseServiceType(id);
        if (serviceType == null) {
            throw new IllegalArgumentException("unknown rpc service type in identify id " + id);
        }
        return serviceType;
    }

    /**
     * 回代校验：拆分字段重组后必须与原 id 逐位相等——杜绝负 index 借位导致的
     * serverId 静默 -1 / index 变 9999 的身份漂移。
     */
    private void checkRoundTrip(long id) {
        long rebuilt = formatId(this.serviceType, this.serverId, parseIndex(id));
        Asserts.checkArgument(rebuilt == id, "identify id {} 拼位不自洽（回代为 {}）", id, rebuilt);
    }

    private static void checkIndex(int index) {
        Asserts.checkArgument(index >= 0 && index < RPC_SERVER_INDEX_SIZE, "index {} 必须在 [0, {}) 内", index, RPC_SERVER_INDEX_SIZE);
    }

    private static void checkServerId(int serverId) {
        // 容量上界 10^11 大于 int 取值域，int 类型天然封顶，仅需下界校验
        Asserts.checkArgument(serverId >= 0, "serverId {} 不得为负", serverId);
    }

    public long getId() {
        return id;
    }

    @Override
    public RpcServiceType getServiceType() {
        return serviceType;
    }

    @Override
    public ContactType getContactType() {
        return serviceType;
    }

    @Override
    public int getServerId() {
        return serverId;
    }

    @Override
    public long getContactId() {
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
        this.serviceType = checkParsedServiceType(id);
        this.serverId = parseServerId(id);
        this.id = id;
        checkRoundTrip(id);
        return this;
    }

    @Override
    public String toString() {
        return "RpcAccessIdentify{" + "id=" + id +
               ", serviceType=" + serviceType +
               ", serverId=" + serverId +
               ", index=" + parseIndex(id) +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RpcAccessIdentify identify)) {
            return false;
        }
        return getContactId() == identify.getContactId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContactId());
    }

}
