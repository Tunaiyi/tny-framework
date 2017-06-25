package com.tny.game.base.item;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author KGTny
 * @ClassName: ItemOwner
 * @Description: 物品项拥有者
 * @date 2011-11-3 上午9:50:52
 * <p>
 * <p>
 * <br>
 */
public interface Owner<M extends ItemModel, S extends Stuff<?>> extends Item<M> {

    /**
     * 获取某事物的信息 <br>
     *
     * @param id 物品ID
     * @return 物品信息
     */
    S getItemByID(long id);

    /**
     * 获取某事物的信息 <br>
     *
     * @param itemID 物品ID
     * @return 物品信息
     */
    default List<S> getItemsByItemID(int itemID) {
        S stuff = getItemByItemID(itemID);
        if (stuff != null) {
            return ImmutableList.of(stuff);
        }
        return ImmutableList.of();
    }

    /**
     * 获取某事物的信息 <br>
     *
     * @param itemID 物品ID
     * @return 物品信息
     */
    S getItemByItemID(int itemID);

}