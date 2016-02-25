package com.tny.game.base.item;

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
public interface Owner<S extends Stuff<?>> {

    /**
     * 获取事物拥有者ID <br>
     *
     * @return
     */
    long getPlayerID();

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
    List<S> getItemByItemID(int itemID);

    /**
     * 获取持有的事物Model类型 <br>
     *
     * @return 物品类型
     */
    <IT extends ItemType> IT getOwnerItemType();

}