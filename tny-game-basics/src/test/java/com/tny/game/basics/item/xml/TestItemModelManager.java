package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.loader.*;

public class TestItemModelManager extends LoadableModelManager<TestItemModel> {

	private final ItemModelContext context;

	private final ModelLoaderFactory factory;

	protected TestItemModelManager(String PATH, ItemModelContext context, ModelLoaderFactory factory) {
		super(TestItemModelImpl.class, TestBehavior.class, TestDemandType.class, TestAction.class, TestAbility.class,
				TestOption.class, PATH);
		this.factory = factory;
		this.context = context;
	}

	protected void initThis() throws Exception {
		this.initManager();
	}

	@Override
	protected ModelLoaderFactory loaderFactory() {
		return factory;
	}

	@Override
	protected ItemModelContext context() {
		return context;
	}

}
