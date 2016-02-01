package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.Attributes;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class GameItemOwner<IM extends ItemModel, S extends Stuff<IM>> extends GameItem<IM> implements Owner<IM, S> {

    /**
     * 扣除事物
     *
     * @param tradeItem  交易对象
     * @param action     交易行为
     * @param attributes 参数
     * @return 返回交易结果
     */
    protected abstract TradeResult consume(TradeItem<? extends IM> tradeItem, Action action, Attributes attributes);

    /**
     * 添加事物
     *
     * @param tradeItem  交易对象
     * @param action     交易行为
     * @param attributes 参数
     * @return 返回交易结果
     */
    protected abstract TradeResult receive(TradeItem<? extends IM> tradeItem, Action action, Attributes attributes);


}
