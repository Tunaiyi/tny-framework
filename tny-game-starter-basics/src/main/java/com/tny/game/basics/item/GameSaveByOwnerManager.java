package com.tny.game.basics.item;

/**
 * 存储当前存储的Item存储在一个Storage上的Manager
 *
 * @author KGTny
 */
public abstract class GameSaveByOwnerManager<S extends Stuff<?>, O extends BaseStuffOwner<?, ?, S>>
        extends GameSaveByHostManager<S, O, GameStuffOwnerManager<O>> {

    protected GameSaveByOwnerManager(Class<? extends S> entityClass) {
        super(entityClass);
    }

    @Override
    protected O findHost(long playerId, long id) {
        GameStuffOwnerManager<O> manager = manager();
        return manager.getOwner(playerId);
    }

    @Override
    protected O itemToHost(S item) {
        GameStuffOwnerManager<O> manager = manager();
        return manager.getOwner(item.getPlayerId());
    }

    @Override
    protected S hostToItem(O host, long id) {
        return host.getItemById(id);
    }

}
