package com.tny.game.base.item;

import java.util.List;

/**
 * 残酷系统
 *
 * @author KGTny
 */
public interface Warehouse<O extends Owner> {

    /**
     * 玩家id
     *
     * @return
     */
    long getPlayerId();

    /**
     * 获取指定itemType的Owner对象
     *
     * @param itemType 事物类型
     * @param clazz    Owner的类型
     * @return 返回owner对象
     */
    <SO extends O> SO getOwner(ItemType itemType, Class<SO> clazz);

    /**
     * 获取指定itemType的Owner对象
     *
     * @param itemType 事物类型
     * @param clazz    Owner的类型
     * @return 返回owner对象
     */
    <I extends Item<?>> I getItemById(ItemType itemType, long id, Class<I> clazz);

    /**
     * 获取指定itemType的Owner对象
     *
     * @param itemType 事物类型
     * @param clazz    Owner的类型
     * @return 返回owner对象
     */
    <I extends Item<?>> List<I> getItemByItemId(ItemType itemType, int itemID, Class<I> clazz);

    //	/**
    //	 * 扣除Trade的物品，并返回修改的owner和item
    //	 *
    //	 * @param result
    //	 *            扣除的物品
    //	 * @return 返回修改的owner和item
    //	 */
    //	DealedResult consume(Trade result, AttributeEntry<?>... entriess);
    //
    //	/**
    //	 * 扣除costNum数量的model事物，并返回修改的owner和item,扣除方式是check
    //	 *
    //	 * @param model
    //	 *            扣除事物的模型
    //	 * @param costNum
    //	 *            扣除数量
    //	 * @param action
    //	 *            通过什么操作扣除
    //	 * @param object
    //	 *            附加参数
    //	 * @return 返回修改的owner和item
    //	 */
    //	DealedResult consume(TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entries);
    //
    //	/**
    //	 * 添加Trade的物品，并返回修改的owner和item
    //	 *
    //	 * @param result
    //	 *            添加的物品
    //	 * @return 返回修改的owner和item
    //	 */
    //	DealedResult receive(Trade result, AttributeEntry<?>... entriess);
    //
    //	/**
    //	 * 添加receiveNum数量的model事物，并返回修改的owner和item,扣除方式是check
    //	 *
    //	 * @param model
    //	 *            添加事物的模型
    //	 * @param receiveNum
    //	 *            添加数量
    //	 * @param action
    //	 *            通过什么操作添加
    //	 * @param object
    //	 *            附加参数
    //	 * @return 返回修改的owner和item
    //	 */
    //	DealedResult receive(TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entriess);

}
