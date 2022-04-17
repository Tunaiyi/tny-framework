package com.tny.game.basics.item.loader;

import java.util.Collection;

/**
 * 模型加载器
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:38
 **/
public interface ModelLoader<M> {

	ModelLoader<M> addPath(String path);

	ModelLoader<M> addPaths(Collection<String> paths);

	ModelLoader<M> addPaths(String... paths);

	ModelLoader<M> addEnumClass(Class<? extends Enum<?>> clazz);

	ModelLoader<M> addEnumClass(Collection<Class<? extends Enum<?>>> classes);

	<C> ModelLoader<M> setContextHandler(ModelLoaderContextHandler<C> contextHandler);

	void load();

}
