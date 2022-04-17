package com.tny.game.basics.item.loader.jackson.yaml;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.expr.*;

/**
 * 模型加载器工厂
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:39
 **/
public class YamlModelLoaderFactory implements ModelLoaderFactory {

	private final ExprHolderFactory exprHolderFactory;

	public YamlModelLoaderFactory(ExprHolderFactory exprHolderFactory) {
		this.exprHolderFactory = exprHolderFactory;
	}

	@Override
	public <M extends Model> ModelLoader<M> createLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler) {
		return new YamlModelLoader<>(modelClass, loadHandler, exprHolderFactory);
	}

}
