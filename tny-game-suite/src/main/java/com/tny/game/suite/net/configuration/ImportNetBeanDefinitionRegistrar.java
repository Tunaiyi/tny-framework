package com.tny.game.suite.net.configuration;

import com.tny.game.common.utils.ExeAide;
import com.tny.game.net.base.*;
import com.tny.game.net.base.configuration.DefaultAppContext;
import com.tny.game.net.codec.v1.DataPacketV1Config;
import com.tny.game.net.endpoint.EndpointKeeperManager;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.suite.net.spring.ReadTimeoutChannelMaker;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.*;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-02 11:45
 */
public class ImportNetBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    private Set<String> getNetNames(NetType netType) {
        List<String> propertiesAppNames = as(environment.getProperty(key(netType, NET_NAMES_NODE), List.class));
        if (propertiesAppNames != null) {
            return new HashSet<>(propertiesAppNames);
        } else {
            return NetConfigurationAide.getNames(environment, netType.getHead());
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(APP_CONTEXT_BEAN_NAME)) {
            registry.registerBeanDefinition(APP_CONTEXT_BEAN_NAME,
                    BeanDefinitionBuilder.genericBeanDefinition(DefaultAppContext.class, () -> Binder.get(environment)
                            .bind(APP_KEY, DefaultAppContext.class)
                            .get())
                            .getBeanDefinition());
        }
        this.registerNetBeanDefinitions(NetType.SERVER, registry);
        this.registerNetBeanDefinitions(NetType.CLIENT, registry);
    }


    public void registerNetBeanDefinitions(NetType netType, BeanDefinitionRegistry registry) {
        Set<String> netNames = getNetNames(netType);
        for (String netName : netNames) {
            String head = key(netType, netName);
            ConfigurationPropertySources.get(environment);
            String encoderName = netName + DataPacketV1Encoder.class.getSimpleName();
            String decoderName = netName + DataPacketV1Decoder.class.getSimpleName();
            String channelMakerName = netName + ChannelMaker.class.getSimpleName();

            // NetUnitSetting Unit配置
            String settingHead = key(head, NET_SETTING_NODE);
            NetUnitSetting setting = netType.createSetting(netName);
            Class<NetUnitSetting> settingClass = as(setting.getClass());
            String settingName = netName + settingClass.getSimpleName();
            registry.registerBeanDefinition(settingName,
                    BeanDefinitionBuilder.genericBeanDefinition(settingClass,
                            () -> Binder.get(environment)
                                    .bind(settingHead, Bindable.ofInstance(setting))
                                    .orElse(setting))
                            .getBeanDefinition());

            // ================================ v1 Packet ================================
            // Encoder 编码
            DataPacketV1Config encoderConfig = Binder.get(environment).bind(key(head, "encode"), DataPacketV1Config.class).get();
            registry.registerBeanDefinition(encoderName,
                    BeanDefinitionBuilder.genericBeanDefinition(DataPacketV1Encoder.class)
                            .addConstructorArgValue(encoderConfig)
                            .getBeanDefinition());
            // Decoder 解码
            DataPacketV1Config decoderConfig = Binder.get(environment).bind(key(head, "decode"), DataPacketV1Config.class).get();
            registry.registerBeanDefinition(decoderName,
                    BeanDefinitionBuilder.genericBeanDefinition(DataPacketV1Decoder.class)
                            .addConstructorArgValue(decoderConfig)
                            .getBeanDefinition());
            // ===========================================================================
            // ChannelMaker 解码
            String channelHead = key(head, "channel");
            String channelMakerClassName = environment.getProperty(key(channelHead, "class"), ReadTimeoutChannelMaker.class.getName());
            Class<Object> channelMakerClass = as(ExeAide.callUnchecked(() -> Class.forName(channelMakerClassName))
                    .orElse(null));
            registry.registerBeanDefinition(channelMakerName,
                    BeanDefinitionBuilder.genericBeanDefinition(channelMakerClass,
                            () -> Binder.get(environment)
                                    .bind(channelHead, channelMakerClass)
                                    .orElseGet(() -> ExeAide.callUnchecked(channelMakerClass::newInstance)))
                            .addPropertyReference("encoder",encoderName)
                            .addPropertyReference("decoder",decoderName)
                            .getBeanDefinition());

            switch (netType) {
                case SERVER:
                    String serverName = netName + ServerGuide.class.getSimpleName();
                    registry.registerBeanDefinition(serverName,
                            BeanDefinitionBuilder.genericBeanDefinition(NettyServerGuide.class)
                                    .addConstructorArgReference(settingName)
                                    .getBeanDefinition());
                    break;
                case CLIENT:
                    String clientName = netName + ClientGuide.class.getSimpleName();
                    registry.registerBeanDefinition(clientName,
                            BeanDefinitionBuilder.genericBeanDefinition(NettyClientGuide.class)
                                    .addConstructorArgReference(settingName)
                                    .addConstructorArgReference(beanName(EndpointKeeperManager.class))
                                    .getBeanDefinition());
                    break;
            }
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
