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

package com.tny.game.net.netty4.cloud.nacos;

import com.alibaba.cloud.nacos.*;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 1:31 下午
 */
public class NacosServeNodeClient extends BaseServeNodeClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(NacosServeNodeClient.class);

    private final NacosDiscoveryProperties properties;

    private final NacosServiceManager nacosServiceManager;

    private final EventListener listener = this::handleEvent;

    public NacosServeNodeClient(NacosDiscoveryProperties properties, NacosServiceManager nacosServiceManager) {
        this.properties = properties;
        this.nacosServiceManager = nacosServiceManager;
    }

    @Override
    protected void doSubscribe(String serveName) {
        NamingService namingService = nacosServiceManager.getNamingService(properties.getNacosProperties());
        try {
            namingService.subscribe(serveName, properties.getGroup(), listener);
        } catch (NacosException e) {
            // 失败上抛：由 holder 记录并进入重试策略，不得静默"已启动未订阅"
            throw new IllegalStateException("nacos subscribe failed", e);
        }
    }

    @Override
    protected void doUnsubscribe(String serveName) {
        NamingService namingService = nacosServiceManager.getNamingService(properties.getNacosProperties());
        try {
            namingService.unsubscribe(serveName, properties.getGroup(), listener);
        } catch (NacosException e) {
            // 失败上抛：由 holder 记录留痕（退订失败不复活订阅）
            throw new IllegalStateException("nacos unsubscribe failed", e);
        }
    }

}
