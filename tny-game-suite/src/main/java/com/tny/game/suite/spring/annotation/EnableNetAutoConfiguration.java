package com.tny.game.suite.spring.annotation;

import com.tny.game.suite.net.configuration.*;
import com.tny.game.suite.net.configuration.command.*;
import com.tny.game.suite.net.configuration.endpoint.*;
import com.tny.game.suite.net.configuration.executor.*;
import com.tny.game.suite.net.configuration.message.*;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
        ImportNetNettyBootstrapDefinitionRegistrar.class,
        ImportMessageDispatcherBeanDefinitionRegistrar.class,
        ImportMessageFactoryBeanDefinitionRegistrar.class,
        ImportSessionKeeperFactoryBeanDefinitionRegistrar.class,
        ImportSessionFactoryBeanDefinitionRegistrar.class,
        ImportSessionSettingBeanDefinitionRegistrar.class,
        ImportTerminalKeeperFactoryBeanDefinitionRegistrar.class,
        ImportTerminalSettingBeanDefinitionRegistrar.class,
        ImportEndpointEventHandlerBeanDefinitionRegistrar.class,
        ImportMessageCommandExecutorBeanDefinitionRegistrar.class,
})
public @interface EnableNetAutoConfiguration {

}
