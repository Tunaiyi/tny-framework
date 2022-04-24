package com.tny.game.net.netty4.cloud.nacos.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.*;
import com.tny.game.net.netty4.cloud.*;
import com.tny.game.net.netty4.cloud.configuration.*;
import com.tny.game.net.netty4.cloud.nacos.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(NacosServiceRegistry.class)
@AutoConfigureAfter(NacosServiceRegistryAutoConfiguration.class)
@AutoConfigureBefore(NetDiscoveryConfiguration.class)
public class NetNacosDiscoveryConfiguration {

    @Bean
    @ConditionalOnBean(NacosDiscoveryProperties.class)
    public ServerGuideRegistrationFactory nacosServerGuideRegistrationFactory(
            NacosDiscoveryProperties properties,
            ObjectProvider<List<NacosRegistrationCustomizer>> registrationCustomizers,
            ObjectProvider<List<LocalServerNodeCustomizer>> localServerNodeCustomizer) {
        return new NacosServerGuideRegistrationFactory(properties,
                registrationCustomizers.getIfAvailable(),
                localServerNodeCustomizer.getIfAvailable());
    }

}

