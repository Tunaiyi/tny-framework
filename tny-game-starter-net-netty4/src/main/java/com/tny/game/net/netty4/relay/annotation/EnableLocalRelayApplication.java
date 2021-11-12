package com.tny.game.net.netty4.relay.annotation;

import com.tny.game.net.netty4.configuration.*;
import com.tny.game.net.netty4.configuration.processor.*;
import com.tny.game.net.netty4.relay.configuration.*;
import com.tny.game.net.netty4.relay.guide.*;
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
		LocalRelayAutoConfiguration.class,
		ImportRelayBootstrapDefinitionRegistrar.class,
		ImportCommandTaskProcessorBeanDefinitionRegistrar.class,
})
public @interface EnableLocalRelayApplication {

}

