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
package com.tny.game.net.relay.packet.arguments;

import com.tny.game.net.base.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:10 下午
 */
public class LinkOpenArguments implements LinkPacketArguments {

    private final String service;

    private final long instance;

    private final String key;

    private final RpcServiceType serviceType;

    public LinkOpenArguments(RpcServiceType serviceType, String service, long instance, String key) {
        this.service = service;
        this.serviceType = serviceType;
        this.instance = instance;
        this.key = key;
    }

    public String getService() {
        return service;
    }

    public long getInstance() {
        return instance;
    }

    public String getKey() {
        return key;
    }

    public RpcServiceType getServiceType() {
        return serviceType;
    }

}
