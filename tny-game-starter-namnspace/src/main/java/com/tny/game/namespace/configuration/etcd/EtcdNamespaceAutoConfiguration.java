package com.tny.game.namespace.configuration.etcd;

import com.tny.game.codec.*;
import com.tny.game.codec.configuration.*;
import com.tny.game.namespace.etcd.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        EtcdNamespaceProperties.class,
})
@AutoConfigureAfter(ObjectCodecAutoConfiguration.class)
public class EtcdNamespaceAutoConfiguration {

    @Bean
    @ConditionalOnBean(ObjectCodecAdapter.class)
    @ConditionalOnClass(EtcdNamespaceExplorerFactory.class)
    EtcdNamespaceExplorerFactory etcdNamespaceExplorerFactory(EtcdNamespaceProperties properties, ObjectCodecAdapter objectCodecAdapter) {
        return new EtcdNamespaceExplorerFactory(properties, null, objectCodecAdapter);
    }

}
