package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.tny.game.common.lifecycle.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-14 14:43
 */
@AsLifecycle(order = Integer.MAX_VALUE)
public class ObjectMapperFactory {

	private static final ObjectMapperFactory FACTORY = new ObjectMapperFactory()
			.addCustomizer((m) -> {
				m.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
				m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				m.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
						.configure(MapperFeature.AUTO_DETECT_GETTERS, false)
						.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
			});

	private static volatile ObjectMapper defaultMapper;

	private static final List<Module> GLOBAL_MODULES = new CopyOnWriteArrayList<>();

	private final List<Module> modules = new CopyOnWriteArrayList<>();

	private final List<ObjectMapperCustomizer> customizers = new CopyOnWriteArrayList<>();

	public static ObjectMapper createMapper() {
		return FACTORY.create();
	}

	/**
	 * @return 默认json mapper
	 */
	public static ObjectMapper defaultMapper() {
		if (defaultMapper != null) {
			return defaultMapper;
		}
		synchronized (ObjectMapperFactory.class) {
			if (defaultMapper != null) {
				return defaultMapper;
			}
			defaultMapper = FACTORY.create();
		}
		return defaultMapper;
	}

	/**
	 * 注册module
	 *
	 * @param module module
	 */
	public static void registerGlobalModule(Module module) {
		GLOBAL_MODULES.add(module);
	}

	@StaticInit
	private static void init() {
		defaultMapper();
	}

	public ObjectMapper create() {
		ObjectMapper mapper = new ObjectMapper();
		mapperGlobalExtension(mapper);
		mapper.registerModules(modules);
		for (ObjectMapperCustomizer customizer : customizers) {
			customizer.customize(mapper);
		}
		return mapper;
	}

	private void mapperGlobalExtension(ObjectMapper mapper) {
		registerGlobalModule(new ExtensionModule());
		registerGlobalModule(new GuavaModule());
		registerGlobalModule(new Jdk8Module());
		mapper.registerModules(GLOBAL_MODULES);
	}

	public ObjectMapperFactory addCustomizer(ObjectMapperCustomizer customizer) {
		customizers.add(customizer);
		return this;
	}

	public ObjectMapperFactory addCustomizers(Collection<ObjectMapperCustomizer> customizers) {
		this.customizers.addAll(customizers);
		return this;
	}

	public ObjectMapperFactory addCustomizers(ObjectMapperCustomizer... customizers) {
		for (ObjectMapperCustomizer customizer : customizers)
			addCustomizers(customizer);
		return this;
	}

	public ObjectMapperFactory addModule(Module module) {
		modules.add(module);
		return this;
	}

	public ObjectMapperFactory addModule(Collection<? extends Module> modules) {
		this.modules.addAll(modules);
		return this;
	}

	public ObjectMapperFactory addModules(Module... modules) {
		for (Module customizer : modules)
			addModule(customizer);
		return this;
	}

}
