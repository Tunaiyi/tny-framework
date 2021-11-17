package com.tny.game.basics.item;

import java.util.*;

/**
 * 存储当前存储的对象存储在另一个对象上的Manager
 *
 * @param <O>
 * @param <SO>
 * @author KGTny
 */
public abstract class GameSaveByOtherManager<O, SO> extends GameCacheManager<O> {

	protected GameSaveByOtherManager(Class<? extends O> entityClass) {
		super(entityClass);
	}

	@Override
	protected O get(long playerId, long id) {
		return getInstance(playerId, id);
	}

	protected abstract O getInstance(long playerId, Object... object);

	/**
	 * 获取 item 存储所在的对象
	 *
	 * @param item 实例
	 * @return 返回存储的实例
	 */
	protected abstract SO getSaveObject(O item);

	/**
	 * 获取 item 存储所在的对象的manager
	 *
	 * @return 返回存储的实例的 manager
	 */
	protected abstract GameManager<SO> manager();

	@Override
	public boolean save(O item) {
		SO object = this.getSaveObject(item);
		Manager<SO> manager = this.manager();
		if (object != null) {
			return manager.save(object);
		}
		return false;
	}

	@Override
	public Collection<O> save(Collection<O> itemCollection) {
		List<O> list = new ArrayList<>();
		for (O item : itemCollection) {
			if (!this.save(item)) {
				list.add(item);
			}
		}
		return list;
	}

	@Override
	public boolean update(O item) {
		SO object = this.getSaveObject(item);
		Manager<SO> manager = this.manager();
		if (object != null) {
			return manager.update(object);
		}
		return false;
	}

	@Override
	public Collection<O> update(Collection<O> itemCollection) {
		List<O> list = new ArrayList<>();
		for (O item : itemCollection) {
			if (!this.update(item)) {
				list.add(item);
			}
		}
		return list;
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
