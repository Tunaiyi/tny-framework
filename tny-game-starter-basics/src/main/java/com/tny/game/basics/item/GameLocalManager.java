package com.tny.game.basics.item;

import java.util.*;

/**
 * 没有任何数据库存储的manager
 *
 * @param <O>
 * @author KGTny
 */
public abstract class GameLocalManager<O> extends GameManager<O> {

	protected GameLocalManager(Class<? extends O> entityClass) {
		super(entityClass);
	}

	@Override
	public boolean save(O item) {
		return true;
	}

	@Override
	public Collection<O> save(Collection<O> itemCollection) {
		return Collections.emptyList();
	}

	@Override
	public boolean update(O item) {
		return true;
	}

	@Override
	public Collection<O> update(Collection<O> itemCollection) {
		return Collections.emptyList();
	}

	@Override
	public boolean insert(O item) {
		return true;
	}

	@Override
	public Collection<O> insert(Collection<O> itemCollection) {
		return Collections.emptyList();
	}

	@Override
	public void delete(O item) {
	}

	@Override
	public void delete(Collection<O> itemCollection) {
	}

}
