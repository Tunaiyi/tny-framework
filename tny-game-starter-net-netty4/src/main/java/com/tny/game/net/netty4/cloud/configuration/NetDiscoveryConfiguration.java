package com.tny.game.net.netty4.cloud.configuration;

import com.tny.game.net.netty4.cloud.*;
import com.tny.game.net.netty4.cloud.nacos.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.cloud.client.serviceregistry.*;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ServiceRegistry.class)
public class NetDiscoveryConfiguration {

    @Bean
    @ConditionalOnBean({ServiceRegistry.class, ServerGuideRegistrationFactory.class})
    public NetAutoServiceRegister netAutoServiceRegister(
            ServiceRegistry<Registration> serviceRegistry, ServerGuideRegistrationFactory registrationFactory) {
        return new NetAutoServiceRegister(serviceRegistry, registrationFactory);
    }

}
