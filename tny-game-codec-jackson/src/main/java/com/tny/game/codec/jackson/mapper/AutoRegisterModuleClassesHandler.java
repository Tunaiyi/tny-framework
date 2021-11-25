package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.scanner.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/22 4:15 下午
 */
@FunctionalInterface
public interface AutoRegisterModuleClassesHandler extends ClassSelectedHandler {

	/**
	 * 创建一个自动注册的Module 的类扫描 handler
	 *
	 * @param handler 处理器
	 * @return 返回
	 */
	static ClassSelectedHandler createHandler(AutoRegisterModuleClassesHandler handler) {
		return handler;
	}

	@Override
	default void selected(Collection<Class<?>> classes) {
		SimpleModule module = new SimpleModule();
		doSelected(module, classes);
		ObjectMapperFactory.registerGlobalModule(module);
	}

	void doSelected(SimpleModule mapper, Collection<Class<?>> classes);

}
