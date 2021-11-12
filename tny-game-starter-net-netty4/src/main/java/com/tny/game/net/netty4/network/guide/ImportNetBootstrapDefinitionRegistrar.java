package com.tny.game.net.netty4.network.guide;

import com.tny.game.boot.registrar.*;
import com.tny.game.net.base.*;
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
import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * <p>
 */
public class ImportNetBootstrapDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		SpringBootNetBootstrapProperties bootstrapConfigure = loadProperties(SpringBootNetBootstrapProperties.class);
		registry.registerBeanDefinition(bootstrapConfigure.getClass().getSimpleName(), BeanDefinitionBuilder
				.genericBeanDefinition(SpringBootNetBootstrapProperties.class, () -> bootstrapConfigure)
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
					.addConstructorArgValue(setting)
					.addConstructorArgReference(channelMaker)
					.getBeanDefinition());
		}
	}

	private String registerChannelMaker(NettyBootstrapSetting setting, BeanDefinitionRegistry registry) {
		NettyDatagramChannelSetting channelSetting = setting.getChannel();
		DatagramPackCodecSetting encoderConfig = channelSetting.getEncoder();
		DatagramPackCodecSetting decoderConfig = channelSetting.getDecoder();
		NettyChannelMakerSetting channelMaker = channelSetting.getMaker();

		DatagramPackV1Encoder encoder = new DatagramPackV1Encoder(encoderConfig);
		DatagramPackV1Decoder decoder = new DatagramPackV1Decoder(decoderConfig);
		String encoderName = unitName(setting.getName(), DatagramPackEncoder.class);
		String decoderName = unitName(setting.getName(), DatagramPackDecoder.class);
		registry.registerBeanDefinition(encoderName,
				BeanDefinitionBuilder.genericBeanDefinition(DatagramPackV1Encoder.class, () -> encoder).getBeanDefinition());
		registry.registerBeanDefinition(decoderName,
				BeanDefinitionBuilder.genericBeanDefinition(DatagramPackV1Decoder.class, () -> decoder).getBeanDefinition());

		Class<DatagramChannelMaker<?>> channelMakerClass = as(channelMaker.getMakerClass());
		DatagramChannelMaker<?> maker;
		try {
			maker = channelMakerClass.newInstance();
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
