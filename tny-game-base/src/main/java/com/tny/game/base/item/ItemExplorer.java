package com.tny.game.base.item;

import java.util.Collection;

/**
 * 事物模型管理器的管理器
 *
 * @author KGTny
 */
public interface ItemExplorer {

    public <I extends Item<?>> I getItem(long playerID, int id, Object... object);

    public boolean inserItem(Item<?>... items);

    public <I extends Item<?>> Collection<I> inserItem(Collection<I> itemCollection);

    public boolean updateItem(Item<?>... items);

    public <I extends Item<?>> Collection<I> updateItem(Collection<I> itemCollection);

    public boolean saveItem(Item<?>... items);

    public <I extends Item<?>> Collection<I> saveItem(Collection<I> itemCollection);

    public boolean deleteItem(Item<?>... items);

    public <I extends Item<?>> Collection<I> deleteItem(Collection<I> itemCollection);

}
