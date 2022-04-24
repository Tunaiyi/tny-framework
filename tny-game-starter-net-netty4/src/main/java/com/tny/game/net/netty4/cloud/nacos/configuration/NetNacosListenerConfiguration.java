package com.tny.game.net.netty4.cloud.nacos.configuration;

import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import com.tny.game.net.netty4.cloud.nacos.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(NacosServiceRegistry.class)
public class NetNacosListenerConfiguration {

    @Bean
    @ConditionalOnBean({NacosServiceRegistry.class})
    public NacosEventListener nacosEventListener(NetAutoServiceRegister register) {
        return new NacosEventListener(register);
    }

}

