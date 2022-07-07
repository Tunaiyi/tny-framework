package com.tny.game.namespace.configuration;

import com.tny.game.namespace.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration(proxyBeanMethods = false)
public class NamespaceAutoConfiguration {

    @Bean
    @ConditionalOnBean(NamespaceExplorerFactory.class)
    NamespaceExplorer etcdNamespaceExplorer(NamespaceExplorerFactory factory) {
        return factory.create();
    }

}
