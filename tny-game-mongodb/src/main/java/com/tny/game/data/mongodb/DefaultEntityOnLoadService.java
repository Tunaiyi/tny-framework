package com.tny.game.data.mongodb;

import com.google.common.collect.ImmutableMap;
import com.tny.game.boot.utils.*;
import com.tny.game.common.lifecycle.*;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/11/4 4:24 下午
 */
public class DefaultEntityOnLoadService implements EntityOnLoadService, AppPrepareStart {

	private final ApplicationContext applicationContext;

	private volatile Map<Class<?>, EntityOnLoad<?>> onLoadMap = ImmutableMap.of();

	public DefaultEntityOnLoadService(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public PrepareStarter getPrepareStarter() {
		return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_6);
	}

	@Override
	public <T> T onLoad(T object) {
		if (object == null) {
			return null;
		}
		EntityOnLoad<T> objectOnLoad = as(onLoadMap.get(object.getClass()));
		if (objectOnLoad == null) {
			return object;
		}
		return objectOnLoad.onLoad(object);
	}

	@Override
	public void prepareStart() {
		Map<Class<?>, EntityOnLoad<?>> onLoadMap = as(
				SpringBeanUtils.beanMapOfType(applicationContext, EntityOnLoad.class, EntityOnLoad::getLoadClass));
		this.onLoadMap = ImmutableMap.copyOf(onLoadMap);
	}

}
