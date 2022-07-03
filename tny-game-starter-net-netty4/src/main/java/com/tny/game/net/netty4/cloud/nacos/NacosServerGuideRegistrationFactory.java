package com.tny.game.net.netty4.cloud.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.tny.game.net.base.*;
import com.tny.game.net.netty4.cloud.*;
import com.tny.game.net.relay.cluster.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.*;

import javax.annotation.Nonnull;
import java.net.*;
import java.util.*;

import static org.springframework.beans.BeanUtils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/9 3:03 下午
 */
public class NacosServerGuideRegistrationFactory implements ServerGuideRegistrationFactory, ApplicationContextAware {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ApplicationContext applicationContext;

    private final NacosDiscoveryProperties defaultDiscoveryProperties;

    private final List<LocalServerNodeCustomizer> localServerNodeCustomizers;

    private final List<NacosRegistrationCustomizer> registrationCustomizers;

    public NacosServerGuideRegistrationFactory(NacosDiscoveryProperties defaultDiscoveryProperties,
            List<NacosRegistrationCustomizer> registrationCustomizers, List<LocalServerNodeCustomizer> localServerNodeCustomizers) {
        this.defaultDiscoveryProperties = defaultDiscoveryProperties;
        if (localServerNodeCustomizers != null) {
            this.localServerNodeCustomizers = localServerNodeCustomizers;
        } else {
            this.localServerNodeCustomizers = ImmutableList.of();
        }
        if (registrationCustomizers != null) {
            this.registrationCustomizers = registrationCustomizers;
        } else {
            this.registrationCustomizers = ImmutableList.of();
        }
    }

    @Override
    public Registration create(ServerGuide guide, NetAppContext netAppContext) {
        String host;
        int port;
        InetSocketAddress address = guide.getServeAddress();
        if (address != null) {
            host = address.getHostName();
            port = address.getPort();
        } else {
            address = guide.getBindAddress();
            port = address.getPort();
            InetAddress inetAddress = address.getAddress();
            host = inetAddress.getHostAddress();
            if (StringUtils.isNoneBlank(host) || "0.0.0.0".equals(host) || "*".equals(host)) {
                host = defaultDiscoveryProperties.getIp();
            }
        }
        LocalServeNode node = new LocalServeNode(netAppContext, guide.discoverService(), guide.serviceName(), guide.getScheme(), host, port);
        for (LocalServerNodeCustomizer customizer : localServerNodeCustomizers) {
            customizer.customize(node);
        }
        return createRegistration(node);
    }

    private NacosRegistration createRegistration(LocalServeNode node) {
        Map<String, String> metadata = new HashMap<>();
        String netMetadata;
        try {
            if (MapUtils.isNotEmpty(node.getMetadata())) {
                netMetadata = objectMapper.writeValueAsString(node.getMetadata());
                metadata.put(NacosMetaDataKey.NET_METADATA, netMetadata);
            }
            metadata.put(NacosMetaDataKey.NET_SERVE_NAME, node.getServeName());
            metadata.put(NacosMetaDataKey.NET_SERVICE, node.getService());
            metadata.put(NacosMetaDataKey.NET_SERVER_ID, node.getId() + "");
            metadata.put(NacosMetaDataKey.NET_APP_TYPE, node.getAppType());
            metadata.put(NacosMetaDataKey.NET_SCOPE_TYPE, node.getScopeType());
            metadata.put(NacosMetaDataKey.NET_SCHEME, node.getScheme());
            metadata.put(NacosMetaDataKey.NET_LAUNCH_TIME, Long.toString(System.currentTimeMillis()));

            NacosDiscoveryProperties properties = new NacosDiscoveryProperties();
            copyProperties(defaultDiscoveryProperties, properties);
            properties.setService(node.discoverService());
            properties.setIp(node.getHost());
            properties.setPort(node.getPort());
            properties.setMetadata(metadata);
            NacosRegistration registration = new NacosRegistration(registrationCustomizers, properties, applicationContext);
            registration.init();
            return registration;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
