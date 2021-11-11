package com.tny.game.net.netty4.relay.guide;

import com.tny.game.boot.registrar.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.netty4.relay.codec.*;
import com.tny.game.net.netty4.relay.codec.arguments.*;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class ImportRelayBootstrapDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		SpringBootRelayBootstrapProperties bootstrapProperties = loadProperties(SpringBootRelayBootstrapProperties.class);
		registry.registerBeanDefinition(bootstrapProperties.getClass().getSimpleName(), BeanDefinitionBuilder
				.genericBeanDefinition(SpringBootRelayBootstrapProperties.class, () -> bootstrapProperties)
				.getBeanDefinition());
		if (bootstrapProperties.getServer() != null) {
			registerServerGuides(Collections.singleton(bootstrapProperties.getServer()), registry);
		}
		if (bootstrapProperties.getClient() != null) {
			registerClientGuides(Collections.singleton(bootstrapProperties.getClient()), registry);
		}
		registerServerGuides(bootstrapProperties.getServers().values(), registry);
		registerClientGuides(bootstrapProperties.getClients().values(), registry);
	}

	private void registerServerGuides(Collection<? extends NettyRelayServerBootstrapSetting> settings, BeanDefinitionRegistry registry) {
		for (NettyRelayServerBootstrapSetting setting : settings) {
			String channelMaker = registerChannelMaker(setting, registry);
			String beanName = setting.getName() + RelayServerGuide.class.getSimpleName();
			registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
					.genericBeanDefinition(NettyRelayServerGuide.class)
					.addConstructorArgValue(setting)
					.addConstructorArgReference(channelMaker)
					.getBeanDefinition());
		}
	}

	private void registerClientGuides(Collection<? extends NettyRelayClientBootstrapSetting> settings, BeanDefinitionRegistry registry) {
		for (NettyRelayClientBootstrapSetting setting : settings) {
			String channelMaker = registerChannelMaker(setting, registry);
			String beanName = setting.getName() + RelayClientGuide.class.getSimpleName();
			registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
					.genericBeanDefinition(NettyRelayClientGuide.class)
					.addConstructorArgValue(setting)
					.addConstructorArgReference(channelMaker)
					.getBeanDefinition());
		}
	}

	private RelayPacketArgumentsCodecService registerRelayPacketArgumentsCodecService(String name, String codeType,
			BeanDefinitionRegistry registry, RelayPacketCodecSetting setting) {
		String messageCodecName = name + codeType + NettyMessageCodec.class;
		BeanDefinitionBuilder messageCodecBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(DefaultNettyMessageCodec.class)
				.addConstructorArgReference(setting.getMessageBodyCodec());
		if (setting.isHasRelayStrategy()) {
			messageCodecBuilder.addConstructorArgReference(setting.getMessageRelayStrategy());
		}
		registry.registerBeanDefinition(messageCodecName, messageCodecBuilder.getBeanDefinition());

		RelayPacketArgumentsCodecService service = new RelayPacketArgumentsCodecService();
		String argumentsCodecServiceName = name + codeType + RelayPacketArgumentsCodecService.class;
		BeanDefinitionBuilder argumentsCodecServiceBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(RelayPacketArgumentsCodecService.class, () -> service)
				.addPropertyReference("messageCodec", messageCodecName);
		registry.registerBeanDefinition(argumentsCodecServiceName, argumentsCodecServiceBuilder.getBeanDefinition());
		return service;
	}

	private String registerChannelMaker(NettyRelayBootstrapSetting setting, BeanDefinitionRegistry registry) {
		NettyRelayChannelSetting channelSetting = setting.getChannel();
		RelayPacketCodecSetting encoderConfig = channelSetting.getEncoder();
		RelayPacketCodecSetting decoderConfig = channelSetting.getDecoder();

		RelayPacketArgumentsCodecService encoderArgumentsCodecService =
				registerRelayPacketArgumentsCodecService(setting.getName(), "Encoder", registry, encoderConfig);
		RelayPacketArgumentsCodecService decoderArgumentsCodecService =
				registerRelayPacketArgumentsCodecService(setting.getName(), "Decoder", registry, decoderConfig);

		RelayPackEncoder encoder = new RelayPackV1Encoder(encoderArgumentsCodecService);
		RelayPackDecoder decoder = new RelayPackV1Decoder(decoderArgumentsCodecService);
		NettyChannelMakerSetting channelMaker = channelSetting.getMaker();
		Class<RelayPackChannelMaker<?>> channelMakerClass = as(channelMaker.getMakerClass());
		RelayPackChannelMaker<?> maker;
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

		String channelMakerName = setting.getName() + RelayPackChannelMaker.class.getSimpleName();
		registry.registerBeanDefinition(channelMakerName, BeanDefinitionBuilder
				.genericBeanDefinition(channelMakerClass, () -> maker)
				.addPropertyValue("channelPipelineChains", channelPipelineChains)
				.getBeanDefinition());
		return channelMakerName;
	}

}
