package com.tny.game.basics.item;

/**
 * 存储当前存储的Item存储在一个Storage上的Manager
 *
 * @author KGTny
 */
public abstract class GameSaveByOwnerManager<S extends Stuff<?>, O extends BaseStuffOwner<?, ?, S>> extends GameSaveByOtherManager<S, O> {

	protected GameSaveByOwnerManager(Class<? extends S> entityClass) {
		super(entityClass);
	}

	@Override
	protected O getSaveObject(S item) {
		GameManager<O> manager = manager();
		return manager.get(item.getPlayerId());
	}

	protected O getSaveObject(long playerId) {
		GameManager<O> manager = manager();
		return manager.get(playerId);
	}

	@Override
	protected S get(long playerId, long id) {
		O owner = this.getSaveObject(playerId);
		return owner.getItemById(id);
	}

	@Override
	protected S getInstance(long playerId, Object... object) {
		O owner = this.getSaveObject(playerId);
		return owner.getItemById((long)object[0]);
	}

}
