package com.tny.game.net.netty4.rpc.service;

import com.tny.game.boot.registrar.*;
import com.tny.game.boot.utils.*;
import com.tny.game.net.netty4.rpc.configuration.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.setting.*;
import org.slf4j.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

/**
 * <p>
 */
public class ImportRpcConnectorDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImportRpcConnectorDefinitionRegistrar.class);

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        RpcProperties rpcProperties = loadProperties(RpcProperties.class);
        RpcClientSetting setting = rpcProperties.getClient();
        if (setting == null) {
            return;
        }
        for (RpcServiceSetting serviceSetting : setting.getServices()) {
            registerRpcConnector(registry, serviceSetting);
        }
    }

    private <T> void registerRpcConnector(BeanDefinitionRegistry registry, RpcServiceSetting serviceSetting) {
        String beanName = BeanNameUtils.lowerCamelName(serviceSetting.serviceName() + RpcClientFactory.class.getSimpleName());
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(RpcClientFactory.class)
                .addAutowiredProperty("appContext")
                .addPropertyValue("setting", serviceSetting);
        if (serviceSetting.isHasGuide()) {
            builder.addPropertyReference("clientGuide", serviceSetting.getGuide());
        } else {
            builder.addAutowiredProperty("clientGuide");
        }
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

}
