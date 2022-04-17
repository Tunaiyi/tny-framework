package com.tny.game.basics.item.loader;

import com.tny.game.basics.item.*;

/**
 * 模型加载器工厂
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:39
 **/
public interface ModelLoaderFactory {

	<M extends Model> ModelLoader<M> createLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler);

}
