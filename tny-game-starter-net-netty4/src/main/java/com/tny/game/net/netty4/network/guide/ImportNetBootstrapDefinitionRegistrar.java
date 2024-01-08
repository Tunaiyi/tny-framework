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
package com.tny.game.net.netty4.network.guide;

import com.tny.game.boot.registrar.*;
import com.tny.game.net.application.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.lifecycle.unit.UnitNames.*;

/**
 * <p>
 */
public class ImportNetBootstrapDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerBy(importingClassMetadata, registry, SpringBootNetBootstrapProperties.class);
        registerBy(importingClassMetadata, registry, SpringBootRpcBootstrapProperties.class);
    }

    private <T extends SpringBootNetBootstrapSettings> void registerBy(@Nonnull AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry, Class<T> propertiesClasses) {
        T bootstrapConfigure = loadProperties(propertiesClasses);
        registry.registerBeanDefinition(bootstrapConfigure.getClass().getSimpleName(), BeanDefinitionBuilder
                .genericBeanDefinition(propertiesClasses, () -> bootstrapConfigure)
                .getBeanDefinition());
        if (bootstrapConfigure.getServer() != null) {
            registerNettyServerGuides(Collections.singleton(bootstrapConfigure.getServer()), registry);
        }
        if (bootstrapConfigure.getClient() != null) {
            registerNettyClientGuides(Collections.singleton(bootstrapConfigure.getClient()), registry);
        }
        registerNettyServerGuides(bootstrapConfigure.getServers().values(), registry);
        registerNettyClientGuides(bootstrapConfigure.getClients().values(), registry);
    }

    private void registerNettyServerGuides(Collection<? extends NettyNetServerBootstrapSetting> settings, BeanDefinitionRegistry registry) {
        for (NettyNetServerBootstrapSetting setting : settings) {
            String channelMaker = registerChannelMaker(setting, registry);
            String beanName = setting.getName() + ServerGuide.class.getSimpleName();
            registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
                    .genericBeanDefinition(NettyServerGuide.class)
                    .addConstructorArgReference("appContext")
                    .addConstructorArgValue(setting)
                    .addConstructorArgReference(channelMaker)
                    .getBeanDefinition());
        }
    }

    private void registerNettyClientGuides(Collection<? extends NettyNetClientBootstrapSetting> settings, BeanDefinitionRegistry registry) {
        for (NettyNetClientBootstrapSetting setting : settings) {
            String channelMaker = registerChannelMaker(setting, registry);
            String beanName = setting.getName() + ClientGuide.class.getSimpleName();
            registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
                    .genericBeanDefinition(NettyClientGuide.class)
                    .addConstructorArgReference("appContext")
                    .addConstructorArgValue(setting)
                    .addConstructorArgReference(channelMaker)
                    .getBeanDefinition());
        }
    }

    private String registerChannelMaker(NettyBootstrapSetting setting, BeanDefinitionRegistry registry) {
        NettyChannelSetting channelSetting = setting.getChannel();
        NetPacketCodecSetting encoderConfig = channelSetting.getEncoder();
        NetPacketCodecSetting decoderConfig = channelSetting.getDecoder();
        NettyChannelMakerSetting channelMaker = channelSetting.getMaker();

        NetPacketV1Encoder encoder = new NetPacketV1Encoder(encoderConfig);
        NetPacketV1Decoder decoder = new NetPacketV1Decoder(decoderConfig);
        String encoderName = unitName(setting.getName(), NetPacketEncoder.class);
        String decoderName = unitName(setting.getName(), NetPacketDecoder.class);
        registry.registerBeanDefinition(encoderName,
                BeanDefinitionBuilder.genericBeanDefinition(NetPacketV1Encoder.class, () -> encoder).getBeanDefinition());
        registry.registerBeanDefinition(decoderName,
                BeanDefinitionBuilder.genericBeanDefinition(NetPacketV1Decoder.class, () -> decoder).getBeanDefinition());

        Class<DatagramChannelMaker<?>> channelMakerClass = as(channelMaker.getMakerClass());
        DatagramChannelMaker<?> maker;
        try {
            maker = channelMakerClass.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        maker.setEncoder(encoder).setCloseOnEncodeError(encoderConfig.isCloseOnError());
        maker.setDecoder(decoder).setCloseOnDecodeError(decoderConfig.isCloseOnError());

        ManagedList<RuntimeBeanReference> channelPipelineChains = new ManagedList<>();
        channelPipelineChains.addAll(channelMaker.getPipelineChains().stream()
                .map(RuntimeBeanReference::new)
                .collect(Collectors.toList()));

        String channelMakerName = setting.getName() + DatagramChannelMaker.class.getSimpleName();
        registry.registerBeanDefinition(channelMakerName, BeanDefinitionBuilder
                .genericBeanDefinition(channelMakerClass, () -> maker)
                .addPropertyValue("channelPipelineChains", channelPipelineChains)
                .getBeanDefinition());
        return channelMakerName;
    }

}
