package com.tny.game.net.netty4.configuration.guide;

import com.tny.game.boot.registrar.*;
import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.codec.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class ImportNetBootstrapDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {
	//    private Set<String> getNetNames(NetType netType) {
	//        List<String> propertiesAppNames = as(this.environment.getProperty(keyOf(netType, NET_NAMES_NODE), List.class));
	//        if (propertiesAppNames != null) {
	//            return new HashSet<>(propertiesAppNames);
	//        } else {
	//            return getNames(this.environment, netType.getHead());
	//        }
	//    }

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

	public void registerNettyServerGuides(Collection<? extends NettyServerBootstrapSetting> settings, BeanDefinitionRegistry registry) {
		for (NettyServerBootstrapSetting setting : settings) {
			NettyChannelSetting channelSetting = setting.getChannel();
			String channelMaker = registerChannelMaker(setting, registry);
			String beanName = setting.getName() + NettyServerGuide.class.getSimpleName();
			registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
					.genericBeanDefinition(NettyServerGuide.class)
					.addConstructorArgValue(setting)
					.addConstructorArgReference(channelMaker)
					.getBeanDefinition());
		}
	}

	private void registerNettyClientGuides(Collection<? extends NettyClientBootstrapSetting> settings, BeanDefinitionRegistry registry) {
		for (NettyClientBootstrapSetting setting : settings) {
			String channelMaker = registerChannelMaker(setting, registry);
			String beanName = setting.getName() + NettyServerGuide.class.getSimpleName();
			registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
					.genericBeanDefinition(NettyClientGuide.class)
					.addConstructorArgValue(setting)
					.addConstructorArgReference(channelMaker)
					.getBeanDefinition());
		}
	}

	private String registerChannelMaker(NettyBootstrapSetting setting, BeanDefinitionRegistry registry) {
		NettyChannelSetting channelSetting = setting.getChannel();
		DataPacketCodecSetting encoderConfig = channelSetting.getEncoder();
		DataPacketCodecSetting decoderConfig = channelSetting.getDecoder();
		NettyChannelMakerSetting channelMaker = channelSetting.getMaker();
		NetPackV1Encoder encoder = new NetPackV1Encoder(encoderConfig);
		NetPackV1Decoder decoder = new NetPackV1Decoder(decoderConfig);
		String encoderBeanName = setting.getName() + NetPackEncoder.class.getSimpleName();
		String decoderBeanName = setting.getName() + NetPackDecoder.class.getSimpleName();
		registry.registerBeanDefinition(encoderBeanName, BeanDefinitionBuilder
				.genericBeanDefinition(NetPackV1Encoder.class, () -> encoder)
				.getBeanDefinition());
		registry.registerBeanDefinition(decoderBeanName, BeanDefinitionBuilder
				.genericBeanDefinition(NetPackV1Decoder.class, () -> decoder)
				.getBeanDefinition());
		String channelMakerClassName = channelMaker.getMakerClass();
		Class<Object> channelMakerClass = as(ExeAide.callNullableWithUnchecked(() -> Class.forName(channelMakerClassName)));
		String channelMakerName = setting.getName() + ChannelMaker.class.getSimpleName();
		BeanDefinitionBuilder channelMakerBuilder = BeanDefinitionBuilder.genericBeanDefinition(channelMakerClass)
				.addPropertyReference("encoder", encoderBeanName)
				.addPropertyReference("decoder", decoderBeanName);
		channelMaker.getProperties().forEach(channelMakerBuilder::addPropertyValue);
		channelMaker.getReferences().forEach(channelMakerBuilder::addPropertyReference);
		registry.registerBeanDefinition(channelMakerName, channelMakerBuilder.getBeanDefinition());
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
