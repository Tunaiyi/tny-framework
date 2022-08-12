/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.cloud.nacos;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.common.collection.map.access.*;
import com.tny.game.net.relay.cluster.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 4:00 下午
 */
public class NacosRemoteServeNode extends RemoteServeNode {

    public NacosRemoteServeNode(Instance instance, ObjectMapper mapper) throws JsonProcessingException {
        Map<String, String> metadata = instance.getMetadata();
        String serveName = metadata.get(NacosMetaDataKey.NET_SERVE_NAME);
        String service = metadata.get(NacosMetaDataKey.NET_SERVICE);
        String serverId = metadata.getOrDefault(NacosMetaDataKey.NET_SERVER_ID, "0");
        String netMetadata = metadata.get(NacosMetaDataKey.NET_METADATA);
        String launchTime = metadata.get(NacosMetaDataKey.NET_LAUNCH_TIME);
        if (launchTime == null || launchTime.isEmpty()) {
            launchTime = "0";
        }
        this.setService(service)
                .setServeName(serveName)
                .setId(Long.parseLong(serverId))
                .setAppType(metadata.get(NacosMetaDataKey.NET_APP_TYPE))
                .setScopeType(metadata.get(NacosMetaDataKey.NET_SCOPE_TYPE))
                .setScheme(metadata.get(NacosMetaDataKey.NET_SCHEME))
                .setHost(instance.getIp())
                .setPort(instance.getPort())
                .setHealthy(instance.isHealthy())
                .setLaunchTime(Long.parseLong(launchTime));
        if (StringUtils.isNoneBlank(netMetadata)) {
            this.setMetadata(mapper.readValue(netMetadata, ObjectMap.class));
        }
    }

    @Override
    protected NacosRemoteServeNode setServeName(String serveName) {
        super.setServeName(serveName);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setService(String service) {
        super.setService(service);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setId(long id) {
        super.setId(id);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setAppType(String appType) {
        super.setAppType(appType);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setScopeType(String scopeType) {
        super.setScopeType(scopeType);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setHealthy(boolean healthy) {
        super.setHealthy(healthy);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setScheme(String scheme) {
        super.setScheme(scheme);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setHost(String host) {
        super.setHost(host);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setPort(int port) {
        super.setPort(port);
        return this;
    }

    @Override
    protected NacosRemoteServeNode setMetadata(Map<String, Object> metadata) {
        super.setMetadata(metadata);
        return this;
    }

}
