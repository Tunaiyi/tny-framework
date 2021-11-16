package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;

import java.util.Collection;

/**
 * 交易对象 获取扣除和奖励的物品信息
 *
 * @author KGTny
 */
public interface TradeInfo {

    /**
     * 获取产生交易结果集的操作
     *
     * @return
     */
    Action getAction();

    /**
     * 获取交易类型(奖励/消耗)
     *
     * @return
     */
    TradeType getTradeType();

    /**
     * 获取该物品需消费其他ItemModel的数量 <br>
     *
     * @param model 消耗的物品
     * @return 返回消耗物品的数量
     */
    Number getNumber(ItemModel model);

    /**
     * 是否需要消耗指定的model物品 <br>
     *
     * @param model 指定物品
     * @return 需要消耗返回true 不需要返回false
     */
    boolean isNeedTrade(ItemModel model);

    /**
     * 获取所有消费项 <br>
     *
     * @return 返沪所有<消费项ID, 数量>
     */
    Collection<TradeItem<ItemModel>> getAllTradeItem();

    /**
     * 获取指定itemType的tradeItem
     *
     * @param itemType 指定类型
     * @return 获取的tradeItem
     */
    Collection<TradeItem<ItemModel>> getTradeItemBy(ItemType... itemType);

    /**
     * 获取指定itemType的tradeItem
     *
     * @param itemType 指定类型
     * @return 获取的tradeItem
     */
    Collection<TradeItem<ItemModel>> getTradeItemBy(Collection<ItemType> itemType);

    /**
     * 是否有itemType的交易物品
     *
     * @param itemType
     * @return
     */
    boolean has(ItemType... itemType);

    /**
     * 交易对象是否为空
     *
     * @return
     */
    boolean isEmpty();

}