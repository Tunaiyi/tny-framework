package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;

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
    public Action getAction();

    /**
     * 获取交易类型(奖励/消耗)
     *
     * @return
     */
    public TradeType getTradeType();

    /**
     * 获取该物品需消费其他ItemModel的数量 <br>
     *
     * @param model 消耗的物品
     * @return 返回消耗物品的数量
     */
    public int getNumber(ItemModel model);

    /**
     * 是否需要消耗指定的model物品 <br>
     *
     * @param model 指定物品
     * @return 需要消耗返回true 不需要返回false
     */
    public boolean isNeedTrade(ItemModel model);

    /**
     * 获取所有消费项 <br>
     *
     * @return 返沪所有<消费项ID, 数量>
     */
    public Collection<TradeItem<ItemModel>> getAllTradeItem();

    /**
     * 获取指定itemType的tradeItem
     *
     * @param itemType 指定类型
     * @return 获取的tradeItem
     */
    public Collection<TradeItem<ItemModel>> getTradeItemBy(ItemType... itemType);

    /**
     * 获取指定itemType的tradeItem
     *
     * @param itemType 指定类型
     * @return 获取的tradeItem
     */
    public Collection<TradeItem<ItemModel>> getTradeItemBy(Collection<ItemType> itemType);

    /**
     * 是否有itemType的交易物品
     *
     * @param itemType
     * @return
     */
    public boolean has(ItemType... itemType);

    /**
     * 交易对象是否为空
     *
     * @return
     */
    public boolean isEmpty();

}
