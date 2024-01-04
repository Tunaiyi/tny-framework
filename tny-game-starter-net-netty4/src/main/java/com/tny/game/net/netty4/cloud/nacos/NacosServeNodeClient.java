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
            e.printStackTrace();
        }
    }

    @Override
    protected void doUnsubscribe(String serveName) {
        NamingService namingService = nacosServiceManager.getNamingService(properties.getNacosProperties());
        try {
            namingService.unsubscribe(serveName, properties.getGroup(), listener);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

}
