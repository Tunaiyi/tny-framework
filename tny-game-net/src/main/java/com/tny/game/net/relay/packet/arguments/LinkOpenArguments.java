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
package com.tny.game.net.relay.packet.arguments;

import com.tny.game.net.application.*;

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
