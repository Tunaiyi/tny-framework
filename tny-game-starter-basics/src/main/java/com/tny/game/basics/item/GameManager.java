package com.tny.game.basics.item;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class GameManager<O> extends GettableManager<O> {

	protected final Class<O> entityClass;

	protected GameManager(Class<? extends O> entityClass) {
		super();
		this.entityClass = as(entityClass);
	}

}