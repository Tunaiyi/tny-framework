package com.tny.game.net.netty4.relay.cluster;

import com.tny.game.boot.registrar.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.datagram.codec.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.netty4.relay.codec.*;
import com.tny.game.net.netty4.relay.codec.arguments.*;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.*;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * <p>
 */
public class ImportRelayServeClusterBootstrapDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		SpringRelayServeClustersProperties properties = loadProperties(SpringRelayServeClustersProperties.class);
		registry.registerBeanDefinition(lowerCamelName(properties.getClass()), BeanDefinitionBuilder
				.genericBeanDefinition(SpringRelayServeClustersProperties.class, () -> properties)
				.getBeanDefinition());
		//		List<RelayServeClusterContext> clusterContexts = new ArrayList<>();
		for (SpringRelayServeClusterSetting setting : properties.getServeClusters()) {
			RelayServeClusterContext clusterContext = registerRelayServeClusterContext(setting, registry);
			//			clusterContexts.add(clusterContext);
		}
		//		RelayServeClusterContextManager relayServeClusterContextManager = new RelayServeClusterContextManager(clusterContexts);
		//		registry.registerBeanDefinition(defaultName(RelayServeClusterContextManager.class), BeanDefinitionBuilder
		//				.genericBeanDefinition(RelayServeClusterContextManager.class, () -> relayServeClusterContextManager)
		//				.getBeanDefinition());
	}

	private RelayServeClusterContext registerRelayServeClusterContext(SpringRelayServeClusterSetting setting, BeanDefinitionRegistry registry) {
		RelayServeClusterContext clusterContext = new RelayServeClusterContext(setting);
		String name = unitName(setting.getId(), RelayServeClusterContext.class);
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RelayServeClusterContext.class, () -> clusterContext)
				.addPropertyReference("clientGuide", setting.getClientGuide());
		if (setting.isHasServeInstanceAllotStrategy()) {
			builder.addPropertyReference("serveInstanceAllotStrategy", setting.getServeInstanceAllotStrategy());
		}
		if (setting.isHasRelayLinkAllotStrategy()) {
			builder.addPropertyReference("relayLinkAllotStrategy", setting.getRelayLinkAllotStrategy());
		}
		registry.registerBeanDefinition(name, builder.getBeanDefinition());
		return clusterContext;
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
				.addConstructorArgReference(setting.getBodyCodec());
		if (setting.isHasRelayStrategy()) {
			messageCodecBuilder.addConstructorArgReference(setting.getRelayStrategy());
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
				registerRelayPacketArgumentsCodecService(setting.getName(), "Decoder", registry, encoderConfig);

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
	//    public void registerNetBeanDefinitions(NetType netType, BeanDefinitionRegistry registry) {
	//        Set<String> netNames = getNetNames(netType);
	//        for (String netName : netNames) {
	//            String appHead = keyOf(netType, netName);
	//            ConfigurationPropertySources.get(this.environment);
	//            String encoderHead = key(appHead, NET_ENCODER_NODE);
	//            String decoderHead = key(appHead, NET_DECODER_NODE);
	//
	//            // NetBootstrapSetting Unit配置
	//            NetBootstrapSetting setting = netType.createSetting(netName);
	//            Class<NetBootstrapSetting> settingClass = as(setting.getClass());
	//            String settingName = netName + NetBootstrapSetting.class;
	//            registry.registerBeanDefinition(settingName,
	//                    BeanDefinitionBuilder.genericBeanDefinition(settingClass,
	//                            () -> Binder.get(this.environment)
	//                                    .bind(appHead, Bindable.ofInstance(setting))
	//                                    .orElse(setting))
	//                            .getBeanDefinition());
	//
	//            // ================================ v1 Packet ================================
	//            // Encoder 编码
	//            // DataPacketV1Config encoderConfig = Binder.get(environment).bind(encoderHead, DataPacketV1Config.class).get();
	//            String encoderConfigName = netName + "Encoder" + DefaultDataPacketV1Config.class.getSimpleName();
	//            registry.registerBeanDefinition(encoderConfigName,
	//                    BeanDefinitionBuilder.genericBeanDefinition(DefaultDataPacketV1Config.class,
	//                            () -> Binder.get(this.environment)
	//                                    .bind(encoderHead, DefaultDataPacketV1Config.class)
	//                                    .orElseGet(DefaultDataPacketV1Config::new))
	//                            .getBeanDefinition());
	//            String encoderName = netName + DataPacketEncoder.class.getSimpleName();
	//            registry.registerBeanDefinition(encoderName,
	//                    BeanDefinitionBuilder.genericBeanDefinition(DataPacketV1Encoder.class)
	//                            .addConstructorArgReference(encoderConfigName)
	//                            .getBeanDefinition());
	//            // Decoder 解码
	//            String decoderConfigName = netName + "Decoder" + DefaultDataPacketV1Config.class.getSimpleName();
	//            registry.registerBeanDefinition(decoderConfigName,
	//                    BeanDefinitionBuilder.genericBeanDefinition(DefaultDataPacketV1Config.class,
	//                            () -> Binder.get(this.environment)
	//                                    .bind(decoderHead, DefaultDataPacketV1Config.class)
	//                                    .orElseGet(DefaultDataPacketV1Config::new))
	//                            .getBeanDefinition());
	//            String decoderName = netName + DataPacketDecoder.class.getSimpleName();
	//            registry.registerBeanDefinition(decoderName,
	//                    BeanDefinitionBuilder.genericBeanDefinition(DataPacketV1Decoder.class)
	//                            .addConstructorArgReference(decoderConfigName)
	//                            .getBeanDefinition());
	//            // ===========================================================================
	//            // ChannelMaker 解码
	//            String channelMakerHead = key(appHead, NET_CHANNEL_NODE);
	//            String channelMakerClassName = this.environment.getProperty(key(channelMakerHead, CLASS_NODE), ReadTimeoutChannelMaker.class
	//            .getName());
	//            Class<Object> channelMakerClass = as(ExeAide.callUnchecked(() -> Class.forName(channelMakerClassName))
	//                    .orElse(null));
	//            String channelMaker = netName + ChannelMaker.class.getSimpleName();
	//            registry.registerBeanDefinition(channelMaker,
	//                    BeanDefinitionBuilder.genericBeanDefinition(channelMakerClass,
	//                            () -> Binder.get(this.environment)
	//                                    .bind(channelMakerHead, channelMakerClass)
	//                                    .orElseGet(() -> ExeAide.callUnchecked(channelMakerClass::newInstance)))
	//                            .addPropertyReference("encoder", encoderName)
	//                            .addPropertyReference("decoder", decoderName)
	//                            .getBeanDefinition());
	//
	//            switch (netType) {
	//                case SERVER:
	//                    // String serverName = netName + ServerGuide.class.getSimpleName();
	//                    registry.registerBeanDefinition(appHead,
	//                            BeanDefinitionBuilder.genericBeanDefinition(NettyServerGuide.class)
	//                                    .addConstructorArgReference(settingName)
	//                                    .getBeanDefinition());
	//                    break;
	//                case CLIENT:
	//                    // String clientName = netName + ClientGuide.class.getSimpleName();
	//                    registry.registerBeanDefinition(appHead,
	//                            BeanDefinitionBuilder.genericBeanDefinition(NettyClientGuide.class)
	//                                    .addConstructorArgReference(settingName)
	//                                    .getBeanDefinition());
	//                    break;
	//            }
	//        }
	//
	//    }

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}
