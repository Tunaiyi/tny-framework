package com.tny.game.net.netty4.relay.cluster;

import com.tny.game.boot.registrar.*;
import com.tny.game.net.netty4.relay.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

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
		for (SpringRelayServeClusterSetting setting : properties.getServeClusters()) {
			registerRelayServeClusterContext(setting, registry);
		}
		//		NettyLocalRelayContext relayContext = new NettyLocalRelayContext();
		//		registry.registerBeanDefinition(lowerCamelName(NettyLocalRelayContext.class), BeanDefinitionBuilder
		//				.genericBeanDefinition(NettyLocalRelayContext.class, () -> relayContext)
		//				.addAutowiredProperty("appContext")
		//				.addAutowiredProperty("relayMessageRouter")
		//				.addAutowiredProperty("serveClusterFilter")
		//				.getBeanDefinition());

	}

	private void registerRelayServeClusterContext(SpringRelayServeClusterSetting setting, BeanDefinitionRegistry registry) {
		NettyLocalServeClusterContext clusterContext = new NettyLocalServeClusterContext(setting);
		String name = unitName(setting.getServeName(), NettyLocalServeClusterContext.class);
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(NettyLocalServeClusterContext.class, () -> clusterContext)
				.addPropertyReference("clientGuide", setting.getClientGuide());
		if (setting.isHasServeInstanceAllotStrategy()) {
			builder.addPropertyReference("serveInstanceAllotStrategy", setting.getServeInstanceAllotStrategy());
		}
		if (setting.isHasRelayLinkAllotStrategy()) {
			builder.addPropertyReference("relayLinkAllotStrategy", setting.getRelayLinkAllotStrategy());
		}
		registry.registerBeanDefinition(name, builder.getBeanDefinition());
	}

}
