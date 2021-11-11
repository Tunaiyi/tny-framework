package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.tny.game.common.lifecycle.annotation.*;
import com.tny.game.scanner.*;

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

	private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

	private static final List<Module> modules = new CopyOnWriteArrayList<>();

	/**
	 * @return 默认json mapper
	 */
	public static ObjectMapper defaultMapper() {
		return DEFAULT_MAPPER;
	}

	/**
	 * 注册module
	 *
	 * @param module module
	 */
	public static void registerModule(Module module) {
		modules.add(module);
	}

	@StaticInit
	private static void init() {
		DEFAULT_MAPPER.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
		DEFAULT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DEFAULT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				.configure(MapperFeature.AUTO_DETECT_GETTERS, false)
				.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
		mapperExtension(DEFAULT_MAPPER);
	}

	public static ObjectMapper mapperExtension(ObjectMapper mapper) {
		registerModule(mapper);
		return mapper;
	}

	public static ObjectMapper createMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapperExtension(mapper);
		return mapper;
	}

	private static void registerModule(ObjectMapper mapper) {
		registerModule(new ExtensionModule());
		registerModule(new GuavaModule());
		registerModule(new Jdk8Module());
		mapper.registerModules(modules);
	}

	/**
	 * 创建一个自动注册的Module 的类扫描 handler
	 *
	 * @param handler 处理器
	 * @return 返回
	 */
	public static ClassSelectedHandler createHandler(AutoRegisterClassSelectedHandler handler) {
		return handler;
	}

	@FunctionalInterface
	public interface AutoRegisterClassSelectedHandler extends ClassSelectedHandler {

		@Override
		default void selected(Collection<Class<?>> classes) {
			SimpleModule module = new SimpleModule();
			doSelected(module, classes);
			ObjectMapperFactory.registerModule(module);
		}

		void doSelected(SimpleModule mapper, Collection<Class<?>> classes);

	}

}
