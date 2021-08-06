package com.tny.game.suite.cache;

import com.tny.game.cache.annotation.*;
import com.tny.game.common.collection.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.base.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.filter.*;
import com.tny.game.suite.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 2017/1/18.
 */
@Component
@Profile(PROTOBUF_MAPPER)
public class ProtobufTableMapperInitiator implements InitializingBean, AppPostStart, ApplicationContextAware {

	private final ClassSelector selector = ClassSelector.create(AnnotationClassFilter.ofInclude(ToCache.class));

	private ApplicationContext context;

	@Resource
	private NetAppContext appContext;

	private ForkJoinTask<?> task;

	public ProtobufTableMapperInitiator() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.task = ForkJoinPool.commonPool()
				.submit(() -> ClassScanner.instance()
						.addSelector(this.selector)
						.scan(this.appContext.getScanPackages()));
	}

	@Override
	public void postStart() throws Exception {
		this.task.join();
		Collection<String> profiles = Configs.getProfiles();
		Map<Class<?>, ProtoCacheFormatter<?, ?>> formatterMap = this.context.getBeansOfType(ProtoCacheFormatter.class)
				.values()
				.stream()
				.map(f -> (ProtoCacheFormatter<?, ?>)f)
				.collect(CollectorsAide.toMap(ProtoCacheFormatter::getClass));
		for (Class<?> clazz : this.selector.getClasses()) {
			ToCache cache = clazz.getAnnotation(ToCache.class);
			String[] cacheProfiles = cache.profiles();
			if (cacheProfiles.length > 0 && Stream.of(cacheProfiles).noneMatch(profiles::contains)) {
				continue;
			}
			String table = cache.prefix();
			if (StringUtils.isBlank(table)) {
				throw new IllegalArgumentException(
						format("{} 没有 prefix 参数", clazz));
			}
			if (cache.triggers().length == 0) {
				continue;
			}
			Class<?> triggerClass = cache.triggers()[0];
			if (!ProtoCacheFormatter.class.isAssignableFrom(triggerClass)) {
				continue;
			}
			ProtoCacheFormatter<?, ?> formatter = formatterMap.get(triggerClass);
			if (formatter == null) {
				throw new IllegalArgumentException(
						format("{} 找不到 {} formatter", clazz, triggerClass));
			}
			ProtobufTableMapper.loadOrCreate(table, formatter);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

}
