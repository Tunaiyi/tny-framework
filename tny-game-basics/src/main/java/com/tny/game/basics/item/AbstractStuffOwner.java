package com.tny.game.basics.item;

import com.tny.game.common.concurrent.collection.*;

import java.util.*;

/**
 * 抽象事物拥有者对象
 *
 * @param <IM>
 * @param <S>
 * @author KGTny
 */
public abstract class AbstractStuffOwner<IM extends ItemModel, SM extends ItemModel, S extends Stuff<SM>>
		extends BaseStuffOwner<IM, SM, S> implements StuffOwner<IM, S> {

	/**
	 * 拥有的item模型
	 */
	//	@Link(name = "itemCollection", ignore = true, ignoreOperation = { Operation.SAVE, Operation.DELETE, Operation.UPDATE })
	protected Map<Long, S> itemMap = new CopyOnWriteMap<>();

	@Override
	public S getItemById(long id) {
		return this.itemMap.get(id);
	}

	@Override
	public S getItemByItemId(int itemId) {
		return this.itemMap.get((long)itemId);
	}

	protected Collection<S> getItemCollection() {
		return Collections.unmodifiableCollection(this.itemMap.values());
	}

	protected void setItemCollection(Collection<S> collection) {
		this.itemMap.clear();
		for (S stuff : collection)
			this.itemMap.put(stuff.getId(), stuff);
	}

}
