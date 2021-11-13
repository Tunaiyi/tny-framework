package com.tny.game.boot.configuration;

import com.tny.game.boot.launcher.*;
import com.tny.game.boot.registrar.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 8:55 下午
 */
@Configuration
//@Import(ImportEventListenerBeanDefinitionRegistrar.class)
public class TnyFrameworkAutoConfiguration {

	@Bean
	public UnitLoadInitiator unitLoadInitiator() {
		return new UnitLoadInitiator();
	}

	@Bean
	public EventListenerInitiator eventListenerInitiator() {
		return new EventListenerInitiator();
	}

	@Bean
	public ApplicationLauncherLifecycle applicationLauncherLifecycle() {
		return new ApplicationLauncherLifecycle();
	}

}
