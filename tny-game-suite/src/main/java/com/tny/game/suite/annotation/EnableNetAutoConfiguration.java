package com.tny.game.suite.annotation;

import com.tny.game.suite.net.configuration.*;
import com.tny.game.suite.net.configuration.executor.*;
import com.tny.game.suite.net.configuration.session.*;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 14:15
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        NetCommonAutoConfiguration.class,
        ImportNetBeanDefinitionRegistrar.class,
        ImportDispatchMessageHandlerBeanDefinitionRegistrar.class,
        ImportPerTunnelDispatchCommandExecutorBeanDefinitionRegistrar.class,
        ImportConcurrentDispatchCommandExecutorBeanDefinitionRegistrar.class,
        ImportSingleTunnelSessionKeeperBeanDefinitionRegistrar.class,
        ImportMultiTunnelSessionKeeperBeanDefinitionRegistrar.class
})
public @interface EnableNetAutoConfiguration {

}
