package com.tny.game.starter.net.netty4.configuration.annotation;

import com.tny.game.starter.net.netty4.configuration.*;
import com.tny.game.starter.net.netty4.configuration.command.*;
import com.tny.game.starter.net.netty4.configuration.endpoint.*;
import com.tny.game.starter.net.netty4.configuration.executor.*;
import com.tny.game.starter.net.netty4.configuration.message.*;
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
        NetAutoConfiguration.class,
        TextFilterAutoConfiguration.class,
        ImportNetNettyBootstrapDefinitionRegistrar.class,
        ImportMessageDispatcherBeanDefinitionRegistrar.class,
        ImportMessageFactoryBeanDefinitionRegistrar.class,
        ImportSessionKeeperFactoryBeanDefinitionRegistrar.class,
        ImportSessionFactoryBeanDefinitionRegistrar.class,
        ImportSessionSettingBeanDefinitionRegistrar.class,
        ImportTerminalKeeperFactoryBeanDefinitionRegistrar.class,
        ImportTerminalSettingBeanDefinitionRegistrar.class,
        ImportCommandTaskProcessorBeanDefinitionRegistrar.class,
})
public @interface EnableNetApplication {

}
