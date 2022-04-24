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
        for (SpringRelayServeClusterSetting setting : properties.getServeClusters()) {
            registerRelayServeClusterContext(setting, registry);
        }
    }

    private void registerRelayServeClusterContext(SpringRelayServeClusterSetting setting, BeanDefinitionRegistry registry) {
        NettyRemoteServeClusterContext clusterContext = new NettyRemoteServeClusterContext(setting);
        String name = unitName(setting.getServeName(), NettyRemoteServeClusterContext.class);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(NettyRemoteServeClusterContext.class, () -> clusterContext)
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
