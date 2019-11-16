package com.tny.game.starter.net.netty4.configuration;

import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.base.configuration.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.starter.net.netty4.configuration.NetEnvironmentAide.*;
import com.tny.game.starter.net.netty4.spring.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.*;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.starter.common.initiator.EnvironmentAide.*;
import static com.tny.game.starter.net.netty4.configuration.NetEnvironmentAide.*;

/**
 * <p>
 */
public class ImportNetNettyBootstrapDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    private Set<String> getNetNames(NetType netType) {
        List<String> propertiesAppNames = as(this.environment.getProperty(keyOf(netType, NET_NAMES_NODE), List.class));
        if (propertiesAppNames != null) {
            return new HashSet<>(propertiesAppNames);
        } else {
            return getNames(this.environment, netType.getHead());
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(APP_CONTEXT_BEAN_NAME)) {
            registry.registerBeanDefinition(APP_CONTEXT_BEAN_NAME,
                    BeanDefinitionBuilder.genericBeanDefinition(DefaultAppContext.class, () -> Binder.get(this.environment)
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
            String appHead = keyOf(netType, netName);
            ConfigurationPropertySources.get(this.environment);
            String encoderHead = key(appHead, NET_ENCODER_NODE);
            String decoderHead = key(appHead, NET_DECODER_NODE);

            // NetBootstrapSetting Unit配置
            NetBootstrapSetting setting = netType.createSetting(netName);
            Class<NetBootstrapSetting> settingClass = as(setting.getClass());
            String settingName = netName + NetBootstrapSetting.class;
            registry.registerBeanDefinition(settingName,
                    BeanDefinitionBuilder.genericBeanDefinition(settingClass,
                            () -> Binder.get(this.environment)
                                        .bind(appHead, Bindable.ofInstance(setting))
                                        .orElse(setting))
                                         .getBeanDefinition());

            // ================================ v1 Packet ================================
            // Encoder 编码
            // DataPacketV1Config encoderConfig = Binder.get(environment).bind(encoderHead, DataPacketV1Config.class).get();
            String encoderConfigName = netName + "Encoder" + SuitDataPacketV1Config.class.getSimpleName();
            registry.registerBeanDefinition(encoderConfigName,
                    BeanDefinitionBuilder.genericBeanDefinition(SuitDataPacketV1Config.class,
                            () -> Binder.get(this.environment)
                                        .bind(encoderHead, SuitDataPacketV1Config.class)
                                        .orElseGet(SuitDataPacketV1Config::new))
                                         .getBeanDefinition());
            String encoderName = netName + DataPacketEncoder.class.getSimpleName();
            registry.registerBeanDefinition(encoderName,
                    BeanDefinitionBuilder.genericBeanDefinition(DataPacketV1Encoder.class)
                                         .addConstructorArgReference(encoderConfigName)
                                         .getBeanDefinition());
            // Decoder 解码
            String decoderConfigName = netName + "Decoder" + SuitDataPacketV1Config.class.getSimpleName();
            registry.registerBeanDefinition(decoderConfigName,
                    BeanDefinitionBuilder.genericBeanDefinition(SuitDataPacketV1Config.class,
                            () -> Binder.get(this.environment)
                                        .bind(decoderHead, SuitDataPacketV1Config.class)
                                        .orElseGet(SuitDataPacketV1Config::new))
                                         .getBeanDefinition());
            String decoderName = netName + DataPacketDecoder.class.getSimpleName();
            registry.registerBeanDefinition(decoderName,
                    BeanDefinitionBuilder.genericBeanDefinition(DataPacketV1Decoder.class)
                                         .addConstructorArgReference(decoderConfigName)
                                         .getBeanDefinition());
            // ===========================================================================
            // ChannelMaker 解码
            String channelMakerHead = key(appHead, NET_CHANNEL_NODE);
            String channelMakerClassName = this.environment.getProperty(key(channelMakerHead, CLASS_NODE), ReadTimeoutChannelMaker.class.getName());
            Class<Object> channelMakerClass = as(ExeAide.callUnchecked(() -> Class.forName(channelMakerClassName))
                                                        .orElse(null));
            String channelMaker = netName + ChannelMaker.class.getSimpleName();
            registry.registerBeanDefinition(channelMaker,
                    BeanDefinitionBuilder.genericBeanDefinition(channelMakerClass,
                            () -> Binder.get(this.environment)
                                        .bind(channelMakerHead, channelMakerClass)
                                        .orElseGet(() -> ExeAide.callUnchecked(channelMakerClass::newInstance)))
                                         .addPropertyReference("encoder", encoderName)
                                         .addPropertyReference("decoder", decoderName)
                                         .getBeanDefinition());

            switch (netType) {
                case SERVER:
                    // String serverName = netName + ServerGuide.class.getSimpleName();
                    registry.registerBeanDefinition(appHead,
                            BeanDefinitionBuilder.genericBeanDefinition(NettyServerGuide.class)
                                                 .addConstructorArgReference(settingName)
                                                 .getBeanDefinition());
                    break;
                case CLIENT:
                    // String clientName = netName + ClientGuide.class.getSimpleName();
                    registry.registerBeanDefinition(appHead,
                            BeanDefinitionBuilder.genericBeanDefinition(NettyClientGuide.class)
                                                 .addConstructorArgReference(settingName)
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
