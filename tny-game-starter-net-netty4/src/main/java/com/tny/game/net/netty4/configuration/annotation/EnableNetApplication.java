package com.tny.game.net.netty4.configuration.annotation;

import com.tny.game.net.netty4.configuration.*;
import com.tny.game.net.netty4.configuration.filter.*;
import com.tny.game.net.netty4.configuration.guide.*;
import com.tny.game.net.netty4.configuration.processor.*;
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
        ImportNetBootstrapDefinitionRegistrar.class,
        ImportCommandTaskProcessorBeanDefinitionRegistrar.class,
})
public @interface EnableNetApplication {

}
